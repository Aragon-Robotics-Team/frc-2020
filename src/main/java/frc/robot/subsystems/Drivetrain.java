package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.NavX;
import frc.robot.util.SparkMaxFactory;

public class Drivetrain extends SubsystemBase {
    public static class Config {
        public int leftMotor;
        public int rightMotor;
        public int leftMotorSlave;
        public int rightMotorSlave;

        public boolean invertAll = false;
        public boolean invertRight = true;

        public double gearRatio;
        public double wheelCircumference; // meters
        public double trackWidth; // meters

        public SimpleMotorFeedforward feedforward;
    }

    private class Motors {
        final Config config;

        CANSparkMax leftMotor;
        CANSparkMax rightMotor;
        CANSparkMax leftMotorSlave;
        CANSparkMax rightMotorSlave;

        private Motors(final Config _config) {
            config = _config;

            leftMotor = SparkMaxFactory.createMaster(config.leftMotor);
            rightMotor = SparkMaxFactory.createMaster(config.rightMotor);
            leftMotorSlave = SparkMaxFactory.createFollower(leftMotor, config.leftMotorSlave);
            rightMotorSlave = SparkMaxFactory.createFollower(rightMotor, config.rightMotorSlave);

            leftMotor.setInverted(config.invertAll);
            rightMotor.setInverted(config.invertAll ^ config.invertRight);
        }
    }

    public class Odometry {
        private static final double updatePeriod = 0.01;

        public double leftVel;
        public double leftPos;

        public double rightVel;
        public double rightPos;

        public Pose2d pose = new Pose2d();

        final Motors motors;
        CANEncoder leftEncoder;
        CANEncoder rightEncoder;

        DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(NavX.getRotation());
        DifferentialDriveKinematics kinematics;
        Notifier notifier = new Notifier(this::update);

        private Odometry(final Motors _motors) {
            motors = _motors;
            final var config = motors.config;

            final var rotationsToMeters = config.gearRatio * config.wheelCircumference;
            final var rpmToMetersPerSecond = rotationsToMeters / 60;

            kinematics = new DifferentialDriveKinematics(config.trackWidth);

            leftEncoder = motors.leftMotor.getEncoder();
            rightEncoder = motors.rightMotor.getEncoder();

            // TODO: Test if SparkMAX ConversionFactor works - maybe implement myself?

            leftEncoder.setPositionConversionFactor(rotationsToMeters);
            leftEncoder.setVelocityConversionFactor(rpmToMetersPerSecond);

            rightEncoder.setPositionConversionFactor(rotationsToMeters);
            rightEncoder.setVelocityConversionFactor(rpmToMetersPerSecond);

            notifier.startPeriodic(updatePeriod);
        }

        public synchronized void resetToPose(final Pose2d pose) {
            leftEncoder.setPosition(0);
            rightEncoder.setPosition(0);

            odometry.resetPosition(pose, NavX.getRotation());
        }

        public synchronized DifferentialDriveWheelSpeeds toWheelSpeeds() {
            return new DifferentialDriveWheelSpeeds(leftVel, rightVel);
        }

        public synchronized ChassisSpeeds toChassisSpeeds() {
            return kinematics.toChassisSpeeds(toWheelSpeeds());
        }

        private synchronized void update() {
            leftVel = leftEncoder.getVelocity();
            leftPos = leftEncoder.getPosition();

            rightVel = rightEncoder.getVelocity();
            rightPos = rightEncoder.getPosition();

            pose = odometry.update(NavX.getRotation(), leftPos, rightPos);
        }
    }

    public class Controller {
        final Motors motors;

        SimpleMotorFeedforward feedforward;

        private Controller(Motors _motors) {
            motors = _motors;
            final var config = motors.config;

            feedforward = config.feedforward;
        }
    }

    Motors motors;
    Odometry odometry;

    public Drivetrain(final Config config) {
        motors = new Motors(config);

        RamseteCommand
        odometry = new Odometry(motors);
    }

    public void driveGood(final DifferentialDriveWheelSpeeds speeds) {
        motors.leftMotor.getPIDController().setReference(99, ControlType.kVelocity, 0, 99, ArbFFUnits.kVoltage);
    }
}

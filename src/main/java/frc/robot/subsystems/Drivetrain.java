package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.NavX;
import frc.robot.util.SparkMaxFactory;

public final class Drivetrain extends SubsystemBase {
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

    private final class Motors {
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

    public final class Odometry {
        private static final double updatePeriod = 0.01;

        public double leftVel;
        public double leftPos;

        public double rightVel;
        public double rightPos;

        public Pose2d pose = new Pose2d();

        final Config config;
        final Motors motors;

        CANEncoder leftEncoder;
        CANEncoder rightEncoder;

        DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(NavX.getRotation());
        DifferentialDriveKinematics kinematics;
        Notifier notifier = new Notifier(this::update);

        private Odometry(final Motors _motors) {
            motors = _motors;
            config = motors.config;

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

        public final synchronized void resetToPose(final Pose2d pose) {
            leftEncoder.setPosition(0);
            rightEncoder.setPosition(0);

            odometry.resetPosition(pose, NavX.getRotation());
        }

        public final synchronized DifferentialDriveWheelSpeeds toWheelSpeeds() {
            return new DifferentialDriveWheelSpeeds(leftVel, rightVel);
        }

        public final synchronized ChassisSpeeds toChassisSpeeds() {
            return kinematics.toChassisSpeeds(toWheelSpeeds());
        }

        private final synchronized void update() {
            leftVel = leftEncoder.getVelocity();
            leftPos = leftEncoder.getPosition();

            rightVel = rightEncoder.getVelocity();
            rightPos = rightEncoder.getPosition();

            pose = odometry.update(NavX.getRotation(), leftPos, rightPos);
        }
    }

    public final class Controller {
        final Config config;
        final Motors motors;
        final Odometry odometry;

        final CANPIDController leftPID;
        final CANPIDController rightPID;

        final SimpleMotorFeedforward feedforward; // TODO: separate left/right ff?

        private Controller(final Odometry _odometry) {
            odometry = _odometry;
            motors = odometry.motors;
            config = odometry.config;

            leftPID = motors.leftMotor.getPIDController();
            rightPID = motors.rightMotor.getPIDController();

            feedforward = config.feedforward;
        }

        // Drive methods:
        // 1. Absolute voltage (l/r voltage directly)
        // 2. Velocity, feedforward only (DifferentialDriveWheelSpeeds) (calls absolute
        // voltage)
        // 3. Arcade, feedforward only (ChassisSpeeds) (calls Velocity ff only)

        // 1
        public final void driveVoltage(double leftVoltage, double rightVoltage) {
            leftPID.setReference(leftVoltage, ControlType.kVoltage);
            rightPID.setReference(rightVoltage, ControlType.kVoltage);
        }

        // 2
        public final void driveVelocityFF(DifferentialDriveWheelSpeeds vel) {
            driveVoltage(feedforward.calculate(vel.leftMetersPerSecond),
                    feedforward.calculate(vel.rightMetersPerSecond));
        }

        // 2
        public final void driveVelocityFF(DifferentialDriveWheelSpeeds vel,
                DifferentialDriveWheelSpeeds accel) {
            driveVoltage(feedforward.calculate(vel.leftMetersPerSecond, accel.leftMetersPerSecond),
                    feedforward.calculate(vel.rightMetersPerSecond, accel.rightMetersPerSecond));
        }

        // 3
        public final void driveChassisFF(ChassisSpeeds speeds) {
            driveVelocityFF(odometry.kinematics.toWheelSpeeds(speeds));
        }
    }

    final Config config;
    final Motors motors;
    final Odometry odometry;
    final Controller controller;

    public Drivetrain(final Config _config) {
        config = _config;
        motors = new Motors(config);
        odometry = new Odometry(motors);
        controller = new Controller(odometry);
    }
}

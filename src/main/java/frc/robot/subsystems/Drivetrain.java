package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.NavX;
import frc.robot.util.SparkMaxFactory;

public class Drivetrain extends SubsystemBase {
    public class DrivetrainConfig {
        public int leftMotor;
        public int rightMotor;
        public int leftMotorSlave;
        public int rightMotorSlave;

        public boolean invertAll = false;
        public boolean invertRight = true;
    }

    private class DrivetrainMotors {
        DrivetrainConfig config;

        CANSparkMax leftMotor;
        CANSparkMax rightMotor;
        CANSparkMax leftMotorSlave;
        CANSparkMax rightMotorSlave;

        private DrivetrainMotors(DrivetrainConfig _config) {
            config = _config;

            leftMotor = SparkMaxFactory.createMaster(config.leftMotor);
            rightMotor = SparkMaxFactory.createMaster(config.rightMotor);
            leftMotorSlave = SparkMaxFactory.createFollower(leftMotor, config.leftMotorSlave);
            rightMotorSlave = SparkMaxFactory.createFollower(rightMotor, config.rightMotorSlave);

            leftMotor.setInverted(config.invertAll);
            rightMotor.setInverted(config.invertAll ^ config.invertRight);

            leftMotor.getPIDController();
        }
    }

    public class Odometry {
        private static final double updatePeriod = 0.01;

        public double leftVel;
        public double leftPos;

        public double rightVel;
        public double rightPos;

        public Pose2d pose = new Pose2d();

        DrivetrainMotors motors;
        CANEncoder leftEncoder;
        CANEncoder rightEncoder;

        DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(NavX.getRotation());
        Notifier notifier = new Notifier(this::update);

        public Odometry(DrivetrainMotors _motors) {
            motors = _motors;

            leftEncoder = motors.leftMotor.getEncoder();
            rightEncoder = motors.rightMotor.getEncoder();

            // TODO: set conversion to meters and meters/second

            notifier.startPeriodic(updatePeriod);
        }

        public void resetToPose(Pose2d pose) {
            leftEncoder.setPosition(0);
            rightEncoder.setPosition(0);

            odometry.resetPosition(pose, NavX.getRotation());
        }

        private void update() {
            leftVel = leftEncoder.getVelocity();
            leftPos = leftEncoder.getPosition();

            rightVel = rightEncoder.getVelocity();
            rightPos = rightEncoder.getPosition();

            pose = odometry.update(NavX.getRotation(), leftPos, rightPos);
        }
    }

    DrivetrainMotors motors;

    public Drivetrain(DrivetrainConfig config) {
        motors = new DrivetrainMotors(config);
    }

    public void driveGood(DifferentialDriveWheelSpeeds speeds) {
        motors.leftMotor.getPIDController().setReference(99, ControlType.kVelocity, 0, 99, ArbFFUnits.kVoltage);
    }
}

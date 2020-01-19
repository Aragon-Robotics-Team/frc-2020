package art840.frc2020.subsystems;

import art840.frc2020.Robot;
import art840.frc2020.util.NavX;
import art840.frc2020.util.SparkMaxFactory;
import art840.frc2020.util.Tuple;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import com.revrobotics.EncoderType;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public final class Drivetrain extends SubsystemBase {
    public static class Config {
        // Motor
        public int leftMotor;
        public int rightMotor;
        public int leftMotorSlave;
        public int rightMotorSlave;

        public boolean invertAll = false;
        public boolean invertRight = true;

        public MotorType motorType = MotorType.kBrushless;
        public int quadratureResolution;

        public boolean invertAllEncoders = false;
        public boolean invertRightEncoders = false;

        // Odometry
        public double gearRatio = 1;
        public double wheelCircumference = 1; // meters
        public double trackWidth = 1; // meters

        // Controller
        public SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0, 1);
        public SlotConfiguration velocityPID = new SlotConfiguration();

        // Director
        public double maxVelocity;
        public double maxAcceleration;
        public double maxRotation;
    }

    private final class Motors {
        CANSparkMax leftMotor;
        CANSparkMax rightMotor;
        CANSparkMax leftMotorSlave;
        CANSparkMax rightMotorSlave;

        private Motors() {
            leftMotor = SparkMaxFactory.createMaster(config.leftMotor, config.motorType);
            rightMotor = SparkMaxFactory.createMaster(config.rightMotor, config.motorType);
            leftMotorSlave = SparkMaxFactory.createFollower(leftMotor, config.leftMotorSlave,
                    config.motorType);
            rightMotorSlave = SparkMaxFactory.createFollower(rightMotor, config.rightMotorSlave,
                    config.motorType);

            leftMotor.setInverted(config.invertAll);
            rightMotor.setInverted(config.invertAll ^ config.invertRight);
        }
    }

    public final class Odometry {
        private static final double updatePeriod = 0.01;

        public final Tuple vel = new Tuple(); // todo: synchronized
        public final Tuple pos = new Tuple();

        private Pose2d pose = new Pose2d();

        CANEncoder leftEncoder;
        CANEncoder rightEncoder;

        DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(NavX.getRotation());
        DifferentialDriveKinematics kinematics;
        Notifier notifier = new Notifier(this::update);

        private Odometry() {
            final var rotationsToMeters = config.gearRatio * config.wheelCircumference;
            final var rpmToMetersPerSecond = rotationsToMeters / 60;

            kinematics = new DifferentialDriveKinematics(config.trackWidth);

            switch (config.motorType) {
                case kBrushless:
                    leftEncoder = motors.leftMotor.getEncoder();
                    rightEncoder = motors.rightMotor.getEncoder();
                case kBrushed:
                    leftEncoder = motors.leftMotor.getEncoder(EncoderType.kQuadrature,
                            config.quadratureResolution);
                    rightEncoder = motors.rightMotor.getEncoder(EncoderType.kQuadrature,
                            config.quadratureResolution);
            }

            leftEncoder.setInverted(config.invertAll ^ config.invertAllEncoders);
            rightEncoder.setInverted(config.invertAll ^ config.invertRight
                    ^ config.invertAllEncoders ^ config.invertRightEncoders);

            leftEncoder.setPositionConversionFactor(rotationsToMeters);
            leftEncoder.setVelocityConversionFactor(rpmToMetersPerSecond);

            rightEncoder.setPositionConversionFactor(rotationsToMeters);
            rightEncoder.setVelocityConversionFactor(rpmToMetersPerSecond);

            notifier.startPeriodic(updatePeriod);
        }

        public final synchronized void resetToPose(final Pose2d pose) {
            leftEncoder.setPosition(0);
            rightEncoder.setPosition(0);

            this.pose = pose;
            odometry.resetPosition(pose, NavX.getRotation());
        }

        public final void resetAll() {
            resetToPose(new Pose2d());
            vel.clear();
            pos.clear();
        }

        public final synchronized DifferentialDriveWheelSpeeds toWheelSpeeds() {
            return vel.get();
        }

        public final synchronized ChassisSpeeds toChassisSpeeds() {
            return kinematics.toChassisSpeeds(toWheelSpeeds());
        }

        private final synchronized void update() {
            vel.set(leftEncoder.getVelocity(), rightEncoder.getVelocity());
            pos.set(leftEncoder.getPosition(), rightEncoder.getPosition());

            pose = odometry.update(NavX.getRotation(), pos.left, pos.right);
        }

        // getters

        public final synchronized Pose2d getPose() {
            return pose;
        }
    }

    public final class Controller {
        public final Tuple savedVel = new Tuple();
        private final Tuple tempAccel = new Tuple();
        private final Tuple tempVoltage = new Tuple();
        private final Tuple tempVel = new Tuple();
        private double lastTime = Timer.getFPGATimestamp();

        final CANPIDController leftPID;
        final CANPIDController rightPID;

        final SimpleMotorFeedforward feedforward; // TODO: separate left/right ff?

        private Controller() {
            leftPID = motors.leftMotor.getPIDController();
            rightPID = motors.rightMotor.getPIDController();

            feedforward = config.feedforward;

            SparkMaxFactory.copyPID(leftPID, config.velocityPID); // TODO: separate left/right pid?
            SparkMaxFactory.copyPID(rightPID, config.velocityPID);
        }

        // Can only be called once per frame
        private final Tuple calcAccel(Tuple vel) {
            // TODO: smoothing?
            var time = Timer.getFPGATimestamp();
            var dt = time - lastTime;

            tempAccel.set((vel.left - savedVel.left) / dt, (vel.right - savedVel.right) / dt);

            lastTime = time;

            return tempAccel;
        }

        private final Tuple calcVoltage(Tuple vel, Tuple accel) {
            return tempVoltage.set(feedforward.calculate(vel.left, accel.left),
                    feedforward.calculate(vel.right, accel.right));
        }

        private final Tuple calcVoltage(Tuple vel) {
            return calcVoltage(vel, calcAccel(vel));
        }

        private final Tuple calcVel(DifferentialDriveWheelSpeeds speeds) {
            return tempVel.set(speeds);
        }

        private final Tuple calcVel(ChassisSpeeds speeds) {
            return calcVel(odometry.kinematics.toWheelSpeeds(speeds));
        }

        public final void reset() {
            savedVel.clear();
            tempAccel.clear();
            tempVel.clear();
            tempVoltage.clear();

            lastTime = Timer.getFPGATimestamp();
        }

        // Drive methods:
        // 0. Raw velocity and feedforward
        // 1. Absolute voltage (l/r voltage directly)
        // 2. Velocity, feedforward only (DifferentialDriveWheelSpeeds) (calls absolute
        // voltage)
        // 3. Arcade, feedforward only (ChassisSpeeds) (calls Velocity ff only)
        // 4. Velocity (with PID)
        // 5. Arcade (with PID) (ChassisSpeeds)

        public final void driveZero() {
            savedVel.clear();
            _driveVoltage(Tuple.zero);
        }

        // // // // // 0
        private final void _driveRawVelocity(Tuple vel, Tuple feedforward) {
            leftPID.setReference(vel.left, ControlType.kVelocity, 0, feedforward.left,
                    ArbFFUnits.kVoltage);
            rightPID.setReference(vel.right, ControlType.kVelocity, 0, feedforward.right,
                    ArbFFUnits.kVoltage);
        }

        // // // // // 1
        private final void _driveVoltage(Tuple voltage) {
            leftPID.setReference(voltage.left, ControlType.kVoltage);
            rightPID.setReference(voltage.right, ControlType.kVoltage);
        }

        public final void driveVoltage(Tuple voltage) {
            savedVel.set(odometry.vel);
            System.out.println(voltage.left + " " + voltage.right);
            _driveVoltage(voltage);
        }

        // // // // // 2
        private final void _driveVelocityFF(Tuple vel, Tuple accel) {
            _driveVoltage(calcVoltage(vel, accel));
        }

        private final void _driveVelocityFF(Tuple vel) {
            _driveVoltage(calcVoltage(vel));
        }

        public final void driveVelocityFF(Tuple vel, Tuple accel) {
            savedVel.set(vel);
            _driveVelocityFF(vel, accel);
        }

        public final void driveVelocityFF(Tuple vel) {
            savedVel.set(vel);

            SmartDashboard.putString("Control Type", "Velocity FF");
            SmartDashboard.putNumber("Want Vel Left", vel.left);
            SmartDashboard.putNumber("Want Vel Right", vel.right);

            SmartDashboard.putNumber("Error Vel Left", vel.left - odometry.vel.left);
            SmartDashboard.putNumber("Error Vel Right", vel.right - odometry.vel.right);

            _driveVelocityFF(vel);
        }

        public final void driveVelocityFF(DifferentialDriveWheelSpeeds speeds) {
            driveVelocityFF(calcVel(speeds));
        }

        // // // // // 3
        public final void driveChassisFF(ChassisSpeeds speeds) {
            driveVelocityFF(calcVel(speeds));
        }

        // // // // // 4
        private final void _driveVelocity(Tuple vel) {
            _driveRawVelocity(vel, calcVoltage(vel));
        }

        private final void _driveVelocity(Tuple vel, Tuple accel) {
            _driveRawVelocity(vel, calcVoltage(vel, accel));
        }

        public final void driveVelocity(Tuple vel) {
            savedVel.set(vel);

            SmartDashboard.putString("Control Type", "Velocity FF + PID");
            SmartDashboard.putNumber("Want Vel Left", vel.left);
            SmartDashboard.putNumber("Want Vel Right", vel.right);

            SmartDashboard.putNumber("Error Vel Left", vel.left - odometry.vel.left);
            SmartDashboard.putNumber("Error Vel Right", vel.right - odometry.vel.right);

            _driveVelocity(vel);
        }

        public final void driveVelocity(Tuple vel, Tuple accel) {
            savedVel.set(vel);
            _driveVelocity(vel, accel);
        }

        public final void driveVelocity(DifferentialDriveWheelSpeeds speeds) {
            driveVelocity(calcVel(speeds));
        }

        // // // // // 5
        public final void driveChassis(ChassisSpeeds speeds) {
            driveVelocity(calcVel(speeds));
        }
    }

    public final class Director {
        RamseteController ramsete = new RamseteController();

        // Autonomous

        public final void setSavedVel(Trajectory.State state) {
            controller.savedVel.set(odometry.kinematics
                    .toWheelSpeeds(new ChassisSpeeds(state.velocityMetersPerSecond, 0,
                            state.curvatureRadPerMeter * state.velocityMetersPerSecond)));
        }

        private final ChassisSpeeds getNextRamsete(Trajectory.State state) {
            return ramsete.calculate(odometry.getPose(), state);
        }

        public final void driveRamseteFF(Trajectory.State state) {
            controller.driveChassisFF(getNextRamsete(state));
        }

        public final void driveRamsete(Trajectory.State state) {
            controller.driveChassis(getNextRamsete(state));
        }

        // Teleop

        /**
         * speeds.left is throttle -1 to 1; speeds.right is rotation -1 to 1 Inverts throttle
         * because chassisspeeds +rotation is CCW
         */
        private final ChassisSpeeds convertJoystickToSpeeds(Tuple speeds) {
            return new ChassisSpeeds(speeds.left * config.maxVelocity, 0,
                    -1 * speeds.right * config.maxRotation);
        }

        public final void driveArcadeFF(Tuple speeds) {
            controller.driveChassisFF(convertJoystickToSpeeds(speeds));
        }

        public final void driveArcade(Tuple speeds) {
            controller.driveChassis(convertJoystickToSpeeds(speeds));
        }

        public final Command driveArcade() {
            return new CommandBase() {
                Tuple tmp = new Tuple();

                public void initialize() {
                    controller.driveZero();
                }

                public void execute() {
                    driveArcade(tmp.set(Robot.j.getThrottle(), Robot.j.getTurn()));
                }

                public void end(boolean i) {
                    controller.driveZero();
                }
            };
        }

        public final Trajectory loadTestPath() {
            Path path = Filesystem.getDeployDirectory().toPath().resolve("Test.wpilib.json");
            try {
                return TrajectoryUtil.fromPathweaverJson(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null; // bruh
            }
        }
    }

    final Config config;
    final Motors motors;
    public final Odometry odometry;
    public final Controller controller;
    public final Director director;

    public Drivetrain(final Config _config) {
        config = _config;
        motors = new Motors();
        odometry = new Odometry();
        controller = new Controller();
        director = new Director();

        var tab = Shuffleboard.getTab("Drivetrain");
        // tab.addNumber("Left Pos", () -> odometry.pos.left).withWidget(BuiltInWidgets.kGraph)
        // .withProperties(Map.of("Visible time", 5));
        // tab.addNumber("Right Pos", () -> odometry.pos.right).withWidget(BuiltInWidgets.kGraph)
        // .withProperties(Map.of("Visible time", 5));

        tab.addNumber("Left Vel", () -> odometry.vel.left).withWidget(BuiltInWidgets.kGraph)
                .withProperties(Map.of("Visible time", 5));
        tab.addNumber("Right Vel", () -> odometry.vel.right).withWidget(BuiltInWidgets.kGraph)
                .withProperties(Map.of("Visible time", 5));

        // tab.addNumber("Pose X", () -> odometry.pose.getTranslation().getX());
        // tab.addNumber("Pose Y", () -> odometry.pose.getTranslation().getY());
        // tab.addNumber("Rotation", () -> odometry.pose.getRotation().getDegrees());
    }
}

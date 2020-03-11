package art840.frc2020.subsystems;

import static java.util.Objects.requireNonNull;

import art840.frc2020.Robot;
import art840.frc2020.util.TrajectoryConfigGen;
import art840.frc2020.util.commands.InstantCommandDisabled;
import art840.frc2020.util.hardware.NavX;
import art840.frc2020.util.hardware.SparkMaxFactory;
import art840.frc2020.util.math.Tuple;
import art840.frc2020.util.telemetry.FalconDashboard;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import com.revrobotics.EncoderType;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
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
        public SimpleMotorFeedforward feedforwardLeft = new SimpleMotorFeedforward(0, 1);
        public SimpleMotorFeedforward feedforwardRight = new SimpleMotorFeedforward(0, 1);
        public SlotConfiguration velocityPID = new SlotConfiguration();

        // Director
        public double maxVelocity;
        public double maxAcceleration;
        public double maxAngularVelocity;
        public double maxAngularAccel;

        public double teleopLinearSlew = 10; // max m / sec ^ 2; no limit = inf
        public double teleopRotationalSlew = 10; // rad / sec ^ 2

        private final void verify() {
            requireNonNull(motorType);
            requireNonNull(feedforwardLeft);
            requireNonNull(feedforwardRight);
            requireNonNull(velocityPID);
        }
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

        public final Tuple vel = new Tuple(); // TODO: synchronized
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
                    break;
                case kBrushed:
                    leftEncoder = motors.leftMotor.getEncoder(EncoderType.kQuadrature,
                            config.quadratureResolution);
                    rightEncoder = motors.rightMotor.getEncoder(EncoderType.kQuadrature,
                            config.quadratureResolution);

                    leftEncoder.setInverted(config.invertAll ^ config.invertAllEncoders);
                    rightEncoder.setInverted(config.invertAll ^ config.invertRight
                            ^ config.invertAllEncoders ^ config.invertRightEncoders);
                    break;
            }

            leftEncoder.setPosition(0);
            leftEncoder.setPositionConversionFactor(rotationsToMeters);
            leftEncoder.setVelocityConversionFactor(rpmToMetersPerSecond);

            rightEncoder.setPosition(0);
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
            resetToPose(new Pose2d(8.5, -4.5, new Rotation2d()));
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

        final SimpleMotorFeedforward feedforwardLeft;
        final SimpleMotorFeedforward feedforwardRight;

        private Controller() {
            leftPID = motors.leftMotor.getPIDController();
            rightPID = motors.rightMotor.getPIDController();

            feedforwardLeft = config.feedforwardLeft;
            feedforwardRight = config.feedforwardRight;

            SparkMaxFactory.copyPID(leftPID, config.velocityPID); // TODO: separate left/right pid?
            SparkMaxFactory.copyPID(rightPID, config.velocityPID);
        }

        // Can only be called once per frame
        private final Tuple calcAccel(Tuple vel) {
            // TODO: smoothing?
            var time = Timer.getFPGATimestamp();
            var dt = time - lastTime;

            tempAccel.set((vel.left - savedVel.left) / dt, (vel.right - savedVel.right) / dt);
            System.out.println("Accel: " + tempAccel);

            lastTime = time;

            return tempAccel;
        }

        private final Tuple calcVoltage(Tuple vel, Tuple accel) {
            return tempVoltage.set(feedforwardLeft.calculate(vel.left, accel.left),
                    feedforwardRight.calculate(vel.right, accel.right));
        }

        private final Tuple calcVoltage(Tuple vel) {
            return calcVoltage(vel, calcAccel(vel));
        }

        private final Tuple calcVel(DifferentialDriveWheelSpeeds speeds) {
            System.out.println(speeds);
            return tempVel.set(speeds);
        }

        private final Tuple calcVel(ChassisSpeeds speeds) {
            System.out.println(speeds);
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
        public final void _driveRawVelocity(Tuple vel, Tuple feedforward) {
            System.out.println("vel: " + vel + " ff: " + feedforward);
            leftPID.setReference(vel.left, ControlType.kVelocity, 0, feedforward.left,
                    ArbFFUnits.kVoltage);
            rightPID.setReference(vel.right, ControlType.kVelocity, 0, feedforward.right,
                    ArbFFUnits.kVoltage);
        }

        // // // // // 1
        public final void _driveVoltage(Tuple voltage) {
            leftPID.setReference(voltage.left, ControlType.kVoltage);
            rightPID.setReference(voltage.right, ControlType.kVoltage);
        }

        public final void driveVoltage(Tuple voltage) {
            savedVel.set(odometry.vel);
            // System.out.println(voltage.left + " " + voltage.right);
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
            _driveVelocityFF(vel, accel);
            savedVel.set(vel);
        }

        public final void driveVelocityFF(Tuple vel) {
            // SmartDashboard.putString("Control Type", "Velocity FF");
            // SmartDashboard.putNumber("Want Vel Left", vel.left);
            // SmartDashboard.putNumber("Want Vel Right", vel.right);

            // SmartDashboard.putNumber("Error Vel Left", vel.left - odometry.vel.left);
            // SmartDashboard.putNumber("Error Vel Right", vel.right - odometry.vel.right);

            _driveVelocityFF(vel);
            savedVel.set(vel);
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
            SmartDashboard.putString("Control Type", "Velocity FF + PID");
            SmartDashboard.putNumber("Want Vel Left", vel.left);
            SmartDashboard.putNumber("Want Vel Right", vel.right);

            SmartDashboard.putNumber("Error Vel Left", vel.left - odometry.vel.left);
            SmartDashboard.putNumber("Error Vel Right", vel.right - odometry.vel.right);

            _driveVelocity(vel);
            savedVel.set(vel);
        }

        public final void driveVelocity(Tuple vel, Tuple accel) {
            _driveVelocity(vel, accel);
            savedVel.set(vel);
        }

        public final void driveVelocity(DifferentialDriveWheelSpeeds speeds) {
            driveVelocity(calcVel(speeds));
        }

        // // // // // 5
        public final void driveChassis(ChassisSpeeds speeds) {
            driveVelocity(calcVel(speeds));
        }
    }

    public final class Auto {
        RamseteController ramsete = new RamseteController();

        public final void setSavedVel(Trajectory.State state) {
            controller.savedVel.set(odometry.kinematics
                    .toWheelSpeeds(new ChassisSpeeds(state.velocityMetersPerSecond, 0,
                            state.curvatureRadPerMeter * state.velocityMetersPerSecond)));
        }

        private final ChassisSpeeds getNextRamsete(Trajectory.State state) {
            FalconDashboard.instance.updatePath(state);

            return ramsete.calculate(odometry.getPose(), state);
        }

        public final void driveRamseteFF(Trajectory.State state) {
            controller.driveChassisFF(getNextRamsete(state));
        }

        public final void driveRamsete(Trajectory.State state) {
            controller.driveChassis(getNextRamsete(state));
        }
    }

    public final class Teleop {
        SlewRateLimiter throttleSlew;
        SlewRateLimiter rotationSlew;

        private Teleop() {
            throttleSlew = new SlewRateLimiter(config.teleopLinearSlew);
            rotationSlew = new SlewRateLimiter(config.teleopRotationalSlew);
        }

        /**
         * speeds.left is throttle -1 to 1; speeds.right is rotation -1 to 1 Inverts rotation
         * because chassisspeeds +rotation is CCW
         */
        private final ChassisSpeeds convertJoystickToSpeeds(Tuple speeds) {
            System.out.println(speeds);

            return new ChassisSpeeds(throttleSlew.calculate(speeds.left * config.maxVelocity), 0,
                    rotationSlew.calculate(-1 * speeds.right * config.maxAngularVelocity));
        }

        public final void reset() {
            // Should be called before and after each drive teleop command
            throttleSlew.reset(0);
            rotationSlew.reset(0);
        }

        public final void driveArcadeFF(Tuple speeds) {
            controller.driveChassisFF(convertJoystickToSpeeds(speeds));
        }

        public final void driveArcade(Tuple speeds) {
            controller.driveChassis(convertJoystickToSpeeds(speeds));
        }

        public final Command driveArcade() {
            return stopInstant().andThen(new WaitCommand(1), new CommandBase() {
                Tuple tmp = new Tuple();

                {
                    addRequirements(Drivetrain.this);
                }

                public void initialize() {
                    controller.driveZero();
                    reset();
                    tmp.clear();
                }

                public void execute() {
                    driveArcadeFF(tmp.set(Robot.joystick.getThrottle(), Robot.joystick.getTurn()));
                }

                public void end(boolean i) {
                    controller.driveZero();
                    reset();
                }
            });
        }

        public final Command stopInstant() {
            return new InstantCommandDisabled(controller::driveZero, Drivetrain.this);
        }
    }

    final Config config;
    final Motors motors;
    public final Odometry odometry;
    public final Controller controller;
    public final Auto auto;
    public final Teleop teleop;

    public final TrajectoryConfigGen configGen;

    public Drivetrain() {
        this(art840.frc2020.map.Map.map.drivetrain);
    }

    public Drivetrain(final Config _config) {
        config = _config;
        config.verify();

        motors = new Motors();
        odometry = new Odometry();
        controller = new Controller();
        auto = new Auto();
        teleop = new Teleop();

        var tab = Shuffleboard.getTab("Drivetrain");
        // tab.addNumber("Left Pos", () -> odometry.pos.left).withWidget(BuiltInWidgets.kGraph)
        // .withProperties(Map.of("Visible time", 5));
        // tab.addNumber("Right Pos", () -> odometry.pos.right).withWidget(BuiltInWidgets.kGraph)
        // .withProperties(Map.of("Visible time", 5));

        tab.addNumber("Left Vel", () -> odometry.vel.left).withWidget(BuiltInWidgets.kGraph)
                .withProperties(Map.of("Visible time", 5, "Automatics bounds", false, "Upper bound",
                        1, "Lower bound", -1))
                .withSize(3, 2).withPosition(0, 0);
        tab.addNumber("Right Vel", () -> odometry.vel.right).withWidget(BuiltInWidgets.kGraph)
                .withProperties(Map.of("Visible time", 5, "Automatics bounds", false, "Upper bound",
                        1, "Lower bound", -1))
                .withSize(3, 2).withPosition(0, 2);

        tab.addNumber("Left Error", () -> controller.savedVel.left - odometry.vel.left)
                .withWidget(BuiltInWidgets.kGraph).withProperties(Map.of("Visible time", 5,
                        "Automatics bounds", false, "Upper bound", 1, "Lower bound", -1))
                .withSize(3, 2).withPosition(3, 0);
        tab.addNumber("Right Error", () -> controller.savedVel.right - odometry.vel.right)
                .withWidget(BuiltInWidgets.kGraph).withProperties(Map.of("Visible time", 5,
                        "Automatics bounds", false, "Upper bound", 1, "Lower bound", -1))
                .withSize(3, 2).withPosition(3, 2);

        tab.addNumber("Left Want", () -> controller.savedVel.left).withWidget(BuiltInWidgets.kGraph)
                .withProperties(Map.of("Visible time", 5, "Automatics bounds", false, "Upper bound",
                        1, "Lower bound", -1))
                .withSize(3, 2).withPosition(6, 0);
        tab.addNumber("Right Want", () -> controller.savedVel.right)
                .withWidget(BuiltInWidgets.kGraph).withProperties(Map.of("Visible time", 5,
                        "Automatics bounds", false, "Upper bound", 1, "Lower bound", -1))
                .withSize(3, 2).withPosition(6, 2);

        // tab.addNumber("Pose X", () -> odometry.pose.getTranslation().getX());
        // tab.addNumber("Pose Y", () -> odometry.pose.getTranslation().getY());
        // tab.addNumber("Rotation", () -> odometry.pose.getRotation().getDegrees());

        FalconDashboard.instance.reset();

        configGen = new TrajectoryConfigGen(new TrajectoryConfigGen.Config() {
            {
                maxVelocity = config.maxVelocity;
                maxAcceleration = config.maxAcceleration;
                maxVoltage = 10;
                maxAngularAccel = config.maxAngularAccel;

                feedforward = config.feedforwardLeft;
                feedforward2 = config.feedforwardRight;
                kinematics = odometry.kinematics;
            }
        });
    }

    public void periodic() {
        FalconDashboard.instance.updateRobot(odometry.getPose());
    }

    public void setBrake(boolean brake) {
        System.out.println("Brake: " + brake);
        // System.out.println("Desired color: " + desiredColor[i]);
        var mode = brake ? IdleMode.kBrake : IdleMode.kCoast;

        motors.leftMotor.setIdleMode(mode);
        motors.rightMotor.setIdleMode(mode);

        if (config.leftMotorSlave != -1) {
            motors.leftMotorSlave.setIdleMode(mode);
        }
        if (config.rightMotorSlave != -1) {
            motors.rightMotorSlave.setIdleMode(mode);
        }
    }
}

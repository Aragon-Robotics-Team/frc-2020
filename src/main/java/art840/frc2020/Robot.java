package art840.frc2020;

import art840.frc2020.commands.drivetrain.auto.FollowTrajectory;
import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.hopper.Hopper;
import art840.frc2020.subsystems.intake.Intake;
import art840.frc2020.subsystems.other.Other;
import art840.frc2020.subsystems.sensors.Sensors;
import art840.frc2020.subsystems.shooter.Shooter;
import art840.frc2020.util.RobotBase;
import art840.frc2020.util.TrajectoryUtil;
import art840.frc2020.util.commands.InstantCommandDisabled;
import art840.frc2020.util.hardware.NavX;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import io.github.oblarg.oblog.Logger;
import java.util.List;

public class Robot extends RobotBase {
    public static Drivetrain drivetrain = new Drivetrain();
    public static Hopper hopper = new Hopper();
    public static Intake intake = new Intake();
    public static Other other = new Other();
    public static Sensors sensors = new Sensors();
    public static Shooter shooter = new Shooter();
    public static Joystick joystick = Map.map.joystick;

    Command waitAndCoast = new WaitCommand(5)
            .andThen(new InstantCommandDisabled(() -> drivetrain.setBrake(false)));

    Trajectory t = TrajectoryGenerator.generateTrajectory(
            new Pose2d(10, -4, new Rotation2d(Math.PI)),
            List.of(new Translation2d(23. / 3., -5), new Translation2d(15.6 / 3., -9.64 / 3.),
                    new Translation2d(10. / 3., -12.7 / 3)),
            new Pose2d(3, -4, new Rotation2d(Math.PI)), drivetrain.configGen.generate());
    Command autoCommand = new FollowTrajectory(t);
    SendableChooser<Command> c = new SendableChooser<Command>();

    public void addAuto(String name) {
        var command = (new FollowTrajectory(TrajectoryUtil.loadGeneratedPath(name)))
                .andThen(drivetrain.controller::driveZero, drivetrain);
        c.addOption(name, command);
    }

    @Override
    public void robotInit() {
        joystick._setup();

        // addAuto("Test");
        // addAuto("ASDF");
        // addAuto("FWD");
        // addAuto("LFWD");
        // addAuto("RFWD");
        // Shuffleboardrivetrain.getTab("Drivetrain").add(c);

        NavX.ahrs.reset();
        drivetrain.odometry.resetAll();

        Logger.configureLoggingAndConfig(this, false);
    }

    @Override
    public void teleopInit() {
        shooter.flywheel.reset();
        shooter.flywheel.beginLogging();
        (new RunCommand(shooter.flywheel::on, shooter.flywheel)).schedule();

        waitAndCoast.cancel();
        drivetrain.setBrake(true);

        drivetrain.odometry.resetAll();
        drivetrain.controller.reset();

        drivetrain.teleop.driveArcade().beforeStarting(drivetrain.controller::driveZero).schedule();

        // Tuple vel = new Tuple(3, 3);
        // Tuple ff = new Tuple(7.5, 7.5);

        // (new WaitCommand(1))
        // .andThen(new RunCommand(() -> drivetrain.controller._driveRawVelocity(vel, ff), d))
        // .schedule();
    }

    @Override
    public void disabledInit() {
        waitAndCoast.schedule();
        shooter.flywheel.reset();
        shooter.flywheel.endLogging();
    }

    @Override
    public void teleopPeriodic() {
        // NetworkTableInstance.getDefault().flush();
        // shooter.on();
    }

    @Override
    public void autonomousInit() {
        waitAndCoast.cancel();
        drivetrain.setBrake(true);

        NavX.ahrs.reset();
        drivetrain.controller.reset();
        drivetrain.odometry.resetAll();

        // (c.getSelected()).schedule();
        // autoCommandrivetrain.schedule();
        // (new WaitCommand(1))
        // .andThen(new RunCommand(
        // () -> drivetrain.controller._driveRawVelocity(Tuple.zero, Tuple.zero), d))
        // .schedule();
        // (new RunCommand((() -> drivetrain.controller._driveVoltage(Tuple.zero)), d)).schedule();
    }
}

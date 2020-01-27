package art840.frc2020;

import art840.frc2020.commands.drivetrain.auto.FollowTrajectory;
import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.Lift;
import art840.frc2020.util.InstantCommandDisabled;
import art840.frc2020.util.NavX;
import art840.frc2020.util.RobotBase;
import art840.frc2020.util.TrajectoryUtil;
import art840.frc2020.util.Tuple;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import java.util.List;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Lift lift = new Lift(Map.map.getLiftConfig());
    public static Joystick j = Map.map.getJoystick();

    // ColorSensor c = ColorSensor.getInstance();

    Command waitAndCoast =
            new WaitCommand(5).andThen(new InstantCommandDisabled(() -> d.setBrake(false)));

    Trajectory t = TrajectoryGenerator.generateTrajectory(
            new Pose2d(10, -4, new Rotation2d(Math.PI)),
            List.of(new Translation2d(23. / 3., -5), new Translation2d(15.6 / 3., -9.64 / 3.),
                    new Translation2d(10. / 3., -12.7 / 3)),
            new Pose2d(3, -4, new Rotation2d(Math.PI)), d.configGen.generate());
    Command autoCommand = new FollowTrajectory(t);
    SendableChooser<Command> c = new SendableChooser<Command>();

    public void addAuto(String name) {
        var command = (new FollowTrajectory(TrajectoryUtil.loadGeneratedPath(name)))
                .andThen(d.controller::driveZero, d);
        c.addOption(name, command);
    }

    @Override
    public void robotInit() {
        // addAuto("Test");
        // addAuto("ASDF");
        // addAuto("FWD");
        // addAuto("LFWD");
        // addAuto("RFWD");
        // Shuffleboard.getTab("Drivetrain").add(c);

        NavX.ahrs.reset();
        d.odometry.resetAll();
    }

    @Override
    public void teleopInit() {
        System.out.println("Works: " + waitAndCoast.runsWhenDisabled());

        waitAndCoast.cancel();
        d.setBrake(true);

        d.odometry.resetAll();
        d.controller.reset();

        d.teleop.driveArcade().beforeStarting(d.controller::driveZero).schedule();

        // Tuple vel = new Tuple(3, 3);
        // Tuple ff = new Tuple(7.5, 7.5);

        // (new RunCommand((() -> d.controller._driveRawVelocity(vel, ff)), d)).schedule();
    }

    @Override
    public void disabledInit() {
        waitAndCoast.schedule();
    }

    @Override
    public void autonomousInit() {
        waitAndCoast.cancel();
        d.setBrake(true);

        NavX.ahrs.reset();
        d.controller.reset();
        d.odometry.resetAll();

        // (c.getSelected()).schedule();
        // autoCommand.schedule();
        (new RunCommand((() -> d.controller._driveRawVelocity(Tuple.zero, Tuple.zero)), d))
                .schedule();
        // (new RunCommand((() -> d.controller._driveVoltage(Tuple.zero)), d)).schedule();
    }
}

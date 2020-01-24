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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Lift lift = new Lift(Map.map.getLiftConfig());
    public static Joystick j = Map.map.getJoystick();

    // ColorSensor c = ColorSensor.getInstance();

    Command waitAndCoast =
            new WaitCommand(5).andThen(new InstantCommandDisabled(() -> d.setBrake(false)));

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

        d.teleop.driveArcade().schedule();
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
        d.odometry.resetAll();

        (c.getSelected()).schedule();
    }
}

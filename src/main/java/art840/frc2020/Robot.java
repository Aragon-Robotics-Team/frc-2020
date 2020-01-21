package art840.frc2020;

import art840.frc2020.commands.drivetrain.auto.FollowTrajectory;
import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.util.NavX;
import art840.frc2020.util.RobotBase;
import art840.frc2020.util.TrajectoryUtil;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Joystick j = Map.map.getJoystick();

    // SplineHelper.getCubicControlVectorsFromWaypoints

    // ColorSensor c = ColorSensor.getInstance();

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
        addAuto("FWD");
        // addAuto("LFWD");
        // addAuto("RFWD");
        Shuffleboard.getTab("Drivetrain").add(c);

        NavX.ahrs.reset();
        d.odometry.resetAll();
    }

    @Override
    public void teleopInit() {
        d.setBrake(true);

        d.odometry.resetAll();

        d.teleop.driveArcade().schedule();
    }

    @Override
    public void disabledInit() {
        d.setBrake(false);
    }

    @Override
    public void autonomousInit() {
        d.setBrake(true);

        NavX.ahrs.reset();
        d.odometry.resetAll();

        (c.getSelected()).schedule();
    }
}

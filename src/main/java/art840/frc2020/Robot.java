package art840.frc2020;

import art840.commands.drivetrain.auto.FollowTrajectory;
import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.util.NavX;
import art840.frc2020.util.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Joystick j = Map.map.getJoystick();
    // ColorSensor c = ColorSensor.getInstance();

    Command auto =
            new FollowTrajectory(d.director.loadTestPath()).andThen(d.controller::driveZero, d);

    @Override
    public void teleopInit() {
        d.odometry.resetAll();

        d.director.driveArcade().schedule();
    }

    @Override
    public void autonomousInit() {
        d.odometry.resetAll();
        NavX.ahrs.reset();

        auto.schedule();
    }
}

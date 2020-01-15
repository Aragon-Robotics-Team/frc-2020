package art840.frc2020;

import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.util.NavX;
import art840.frc2020.util.RobotBase;
import edu.wpi.first.wpilibj.geometry.Pose2d;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Joystick j = Map.map.getJoystick();
    // ColorSensor c = ColorSensor.getInstance();

    @Override
    public void teleopInit() {
        d.odometry.resetToPose(new Pose2d());
        d.director.driveArcadeVSimpleCommand().schedule();
    }

    @Override
    public void autonomousInit() {
        d.odometry.resetToPose(new Pose2d());
        NavX.ahrs.reset();
    }
}

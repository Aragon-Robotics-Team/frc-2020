package art840.frc2020;

import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.util.NavX;
import art840.frc2020.util.RobotBase;

public class Robot extends RobotBase {
    public static Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    public static Joystick j = Map.map.getJoystick();
    // ColorSensor c = ColorSensor.getInstance();

    @Override
    public void teleopInit() {
        d.odometry.resetAll();
        d.director.driveArcadeVSimpleCommand().schedule();
    }

    @Override
    public void autonomousInit() {
        d.odometry.resetAll();
        NavX.ahrs.reset();
    }
}

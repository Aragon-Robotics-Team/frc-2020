package art840.frc2020;

import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.ColorSensor;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.util.RobotBase;

public class Robot extends RobotBase {
    Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    Joystick j = Map.map.getJoystick();
    ColorSensor c = ColorSensor.getInstance();
}

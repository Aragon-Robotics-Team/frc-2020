package frc.robot;

import frc.robot.map.Map;
import frc.robot.oi.Joystick;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.Drivetrain;
import frc.robot.util.RobotBase;

public class Robot extends RobotBase {
    Drivetrain d = new Drivetrain(Map.map.getDrivetrainConfig());
    Joystick j = Map.map.getJoystick();
    ColorSensor c = ColorSensor.getInstance();
}

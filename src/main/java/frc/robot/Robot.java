package frc.robot;

import frc.robot.map.Map;
import frc.robot.oi.Joystick;
import frc.robot.subsystems.Drivetrain;
import frc.robot.util.RobotBase;

public class Robot extends RobotBase {
    Map map = Map.getMap();

    Drivetrain d = new Drivetrain(map.getDrivetrainConfig());
    Joystick j = map.getJoystick();
}

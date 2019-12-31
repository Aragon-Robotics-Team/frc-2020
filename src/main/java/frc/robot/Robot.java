package frc.robot;

import frc.robot.map.Map;
import frc.robot.subsystems.Drivetrain;
import frc.robot.util.RobotBase;

public class Robot extends RobotBase {
    Drivetrain d = new Drivetrain(Map.getMap().getDrivetrainConfig());
}

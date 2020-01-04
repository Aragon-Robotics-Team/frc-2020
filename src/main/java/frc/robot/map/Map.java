package frc.robot.map;

import frc.robot.oi.Joystick;
import frc.robot.subsystems.Drivetrain;

public abstract class Map {
    public static final Map map = new TestMap();

    public abstract Drivetrain.Config getDrivetrainConfig();

    public abstract Joystick getJoystick();
}

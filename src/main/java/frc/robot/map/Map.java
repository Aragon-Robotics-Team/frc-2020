package frc.robot.map;

import frc.robot.subsystems.Drivetrain;

public abstract class Map {
    public static Map getMap() {
        return new TestMap();
    }

    public abstract Drivetrain.Config getDrivetrainConfig();
}

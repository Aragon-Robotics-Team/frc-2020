package art840.frc2020.map;

import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.Lift;

public abstract class Map {
    public static final Map map = new TestMap();

    public abstract Drivetrain.Config getDrivetrainConfig();

    public abstract int getPCMId();

    public abstract Lift.Config getLiftConfig();

    public abstract Joystick getJoystick();
}

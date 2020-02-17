package art840.frc2020.map;

import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.Lift;
import art840.frc2020.subsystems.Shooter;
import art840.frc2020.subsystems.WheelSpinner;

public abstract class Map {
    public static final Map map = new TestMap();

    public abstract Joystick getJoystick();

    public abstract Drivetrain.Config getDrivetrainConfig();

    public abstract Lift.Config getLiftConfig();

    public abstract WheelSpinner.Config getWheelSpinnerConfig();

    public abstract Shooter.Config getShooterConfig();

    public abstract int getPCMId();
}

package art840.frc2020.map;

import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.hopper.Funnel;
import art840.frc2020.subsystems.shooter.Hood;
import art840.frc2020.subsystems.shooter.Flywheel;
import art840.frc2020.subsystems.hopper.Tower;
import art840.frc2020.subsystems.shooter.Turret;
import art840.frc2020.subsystems.other.Climb;
import art840.frc2020.subsystems.intake.Rollers;
import art840.frc2020.subsystems.other.WheelSpinner;

public class Map {
    public static final Map map = new TestMap();

    public Joystick joystick;

    public Drivetrain.Config drivetrain;

    public static class Hopper {
        public Funnel.Config funnel;
        public Hood.Config hood;
        public Flywheel.Config shooter;
        public Tower.Config tower;
        public Turret.Config turret;
    }

    public Hopper hopper = new Hopper();

    public static class Intake {
        public Climb.Config climb;
        public Rollers.Config rollers;
    }

    public Intake intake = new Intake();

    public static class Other {
        public WheelSpinner.Config wheelSpinner;
        public int pcmId;
    }

    public Other other = new Other();
}

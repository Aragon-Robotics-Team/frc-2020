package art840.frc2020.subsystems.shooter;

// public class Shooter implements Loggable {
public class Shooter {
    public final Flywheel flywheel = new Flywheel();
    public final Hood hood = new Hood();
    public final Turret turret = new Turret();

    // @Override
    public boolean skipLayout() {
        return true;
    }
}

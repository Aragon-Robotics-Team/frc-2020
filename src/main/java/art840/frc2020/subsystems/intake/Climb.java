package art840.frc2020.subsystems.intake;

import art840.frc2020.map.Map;
import art840.frc2020.util.SensorFactory;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climb extends SubsystemBase {
    public static class Config {
        public int solenoidFwd;
        public int solenoidRev;
    }

    final Config config;

    public final DoubleSolenoid sol;

    public Climb() {
        this(Map.map.intake.climb);
    }

    public Climb(Config config) {
        this.config = config;
        sol = SensorFactory.createDoubleSolenoid(config.solenoidFwd, config.solenoidRev);
    }
}

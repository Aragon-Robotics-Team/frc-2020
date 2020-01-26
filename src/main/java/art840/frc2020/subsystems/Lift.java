package art840.frc2020.subsystems;

import art840.frc2020.map.Map;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Lift extends SubsystemBase {
    public static class Config {
        public int solenoidFwd;
        public int solenoidRev;
    }

    final Config config;

    public final DoubleSolenoid sol;

    public Lift(Config config) {
        this.config = config;
        sol = new DoubleSolenoid(Map.map.getPCMId(), config.solenoidFwd, config.solenoidRev);
    }
}

package art840.frc2020.util.hardware;

import art840.frc2020.map.Map;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class SensorFactory {
    private SensorFactory() {}

    public static final Solenoid createSolenoid(int port) {
        return new Solenoid(Map.map.other.pcmId, port);
    }

    public static final DoubleSolenoid createDoubleSolenoid(int portFwd, int portRev) {
        return new DoubleSolenoid(Map.map.other.pcmId, portFwd, portRev);
    }
}

package art840.frc2020.util.hardware;

import art840.frc2020.map.Map;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.BooleanSupplier;

public class SensorFactory {
    private SensorFactory() {}

    public static final Solenoid createSolenoid(int port) {
        return new Solenoid(Map.map.other.pcmId, port);
    }

    public static final DoubleSolenoid createDoubleSolenoid(int portFwd, int portRev) {
        return new DoubleSolenoid(Map.map.other.pcmId, portFwd, portRev);
    }

    // Todo: deadband?
    public static final Trigger createDigitalInput(int port, boolean invert) {
        BooleanSupplier get;

        if (port >= 0) {
            BooleanSupplier rawGet = (new DigitalInput(port))::get;
            if (invert) {
                get = () -> !rawGet.getAsBoolean();
            } else {
                get = rawGet;
            }
        } else {
            get = () -> false;
        }

        return new Trigger(get);
    }
}

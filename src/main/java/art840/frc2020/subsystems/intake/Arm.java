package art840.frc2020.subsystems.intake;

import art840.frc2020.map.Map;
import art840.frc2020.util.SensorFactory;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Arm {
    public static class Config {
        public int solenoidFwd;
        public int solenoidRev;
        public boolean disabled = false;
    }

    public static enum Position {
        In, Out;
    }

    final Config config;
    final DoubleSolenoid solenoid;
    private Position position;

    public Arm() {
        this(Map.map.intake.arm);
    }

    public Arm(Config _config) {
        config = _config;
        if (!config.disabled) {
            solenoid = SensorFactory.createDoubleSolenoid(config.solenoidFwd, config.solenoidRev);
        } else {
            solenoid = null;
        }

        set(Position.In);
    }

    private void _set(Value postion) {
        if (!config.disabled) {
            solenoid.set(postion);
        }
    }

    public void set(Position _position) {
        position = _position;

        switch (position) {
            case In:
                _set(Value.kForward);
                break;
            case Out:
                _set(Value.kReverse);
                break;
        }
    }
}

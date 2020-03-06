package art840.frc2020.subsystems.shooter;

import art840.frc2020.map.Map;
import art840.frc2020.util.hardware.SensorFactory;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hood extends SubsystemBase {
    public static class Config {
        public int solenoidFwd;
        public int solenoidRev;
        public boolean disabled = true;
    }

    public static enum Position {
        In, Out;
    }

    final Config config;
    final DoubleSolenoid solenoid;
    private Position position;

    public Hood() {
        this(Map.map.shooter.hood);
    }

    public Hood(Config _config) {
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

    public Command setCommand(Position position) {
        return new InstantCommand(() -> set(position), this);
    }
}

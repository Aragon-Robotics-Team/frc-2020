package art840.frc2020.subsystems.other;

import art840.frc2020.map.Map;
import art840.frc2020.util.commands.RunEndCommand;
import art840.frc2020.util.hardware.SensorFactory;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climb extends SubsystemBase {
    public static class Config {
        public int solenoidLeftFwd;
        public int solenoidLeftRev;
        public int solenoidRightFwd;
        public int solenoidRightRev;
        public boolean disabled = true;
    }

    public static enum Position {
        In, Out;
    }

    final Config config;
    final DoubleSolenoid solenoidLeft;
    final DoubleSolenoid solenoidRight;
    private Position position;

    public Climb() {
        this(Map.map.other.climb);
    }

    public Climb(Config _config) {
        config = _config;
        if (!config.disabled) {
            solenoidLeft = SensorFactory.createDoubleSolenoid(config.solenoidLeftFwd,
                    config.solenoidLeftRev);
            solenoidRight = SensorFactory.createDoubleSolenoid(config.solenoidRightFwd,
                    config.solenoidRightRev);
        } else {
            solenoidLeft = null;
            solenoidRight = null;
        }

        set(Position.In);

        setDefaultCommand(new RunCommand(() -> set(Position.In), this));
    }

    private void _set(Value postion) {
        if (!config.disabled) {
            solenoidLeft.set(postion);
            solenoidRight.set(postion);
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

    public final Command keepOutCommand() {
        return new RunEndCommand(() -> set(Position.Out), () -> set(Position.In), this);
    }

    public final Command setOutCommand() {
        return new InstantCommand(() -> set(Position.Out), this);
    }
}

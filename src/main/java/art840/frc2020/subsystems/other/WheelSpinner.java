package art840.frc2020.subsystems.other;

import art840.frc2020.map.Map;
import art840.frc2020.util.commands.InstantCommandDisabled;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WheelSpinner extends SubsystemBase {
    public final TalonSRX motor;
    private final Config config;

    public static class Config {
        public int motorControllerPort;
        public double maxSpeed;
        public double rampTime;
        public boolean invert;
    }

    public WheelSpinner() {
        this(Map.map.other.wheelSpinner);
    }

    public WheelSpinner(final Config _config) {
        config = _config;
        motor = new TalonSRX(config.motorControllerPort);
        motor.configOpenloopRamp(config.rampTime);
        motor.configClosedloopRamp(0);
    }

    public void set(boolean spin) {
        // true == clockwise
        motor.set(ControlMode.PercentOutput, (spin ^ config.invert ? 1 : -1) * config.maxSpeed);
    }

    public void stop() {
        motor.set(ControlMode.PercentOutput, 0);
    }

    public Command stopCommand() {
        return new InstantCommandDisabled(this::stop, this);
    }
}

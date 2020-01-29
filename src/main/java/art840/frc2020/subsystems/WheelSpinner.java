package art840.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WheelSpinner extends SubsystemBase {
    private TalonSRX motor;
    private Config config;

    public static class Config {
        public int motorControllerPort;
        public double maxSpeed;
        public double rampTime;
    }

    public WheelSpinner(final Config _config) {
        config = _config;
        motor = new TalonSRX(config.motorControllerPort);
        motor.configOpenloopRamp(config.rampTime);
        motor.configClosedloopRamp(0);
    }

    public void set(boolean spin) {
        if (spin == true) {
            motor.set(ControlMode.PercentOutput, config.maxSpeed);
        } else {
            motor.set(ControlMode.PercentOutput, -config.maxSpeed);
        }
    }

    public void stop() {
        motor.set(ControlMode.PercentOutput, 0);
    }
}

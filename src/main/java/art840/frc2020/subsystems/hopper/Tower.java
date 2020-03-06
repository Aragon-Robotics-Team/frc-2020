package art840.frc2020.subsystems.hopper;

import art840.frc2020.map.Map;
import art840.frc2020.util.TalonSRXWrapper;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Tower extends SubsystemBase {
    public static class Config {
        // Motor
        public int motorPort;

        public boolean invert = false;

        // Controller
        public double voltsFull;
        public double voltsReverse;
        public double rampTime;
    }

    final Config config;

    final TalonSRXWrapper motor;

    public Tower() {
        this(Map.map.hopper.tower);
    }

    public Tower(Config _config) {
        config = _config;

        motor = new TalonSRXWrapper(new TalonSRXWrapper.Config() {
            {
                port = config.motorPort;
                invert = config.invert;

                openloopRamp = config.rampTime;
                closedloopRamp = config.rampTime;
            }
        });

        motor.setBrake(false);
    }

    public void setFwd() {
        motor.setVoltage(config.voltsFull);
    }

    public void setRev() {
        motor.setVoltage(-config.voltsReverse);
    }

    public void stop() {
        motor.setVoltage(0);
    }
}

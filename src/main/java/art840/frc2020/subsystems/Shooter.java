package art840.frc2020.subsystems;

import art840.frc2020.map.Map;
import art840.frc2020.util.SparkMaxFactory;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    public static class Config {
        public int motorPort;
        public boolean motorInvert;

        public int encoderPortA;
        public int encoderPortB;
        public boolean encoderInvert;
        public double encoderResolution; // 1x

        public double speedRPM;
        public double rampTime; // seconds to ramp, not acceleration

        // Everything: volts, rpm
        public double kP;
        public double constantFF;
    }

    final Config config;

    final CANSparkMax motor;
    final Encoder encoder;

    final PIDController pid;
    final SlewRateLimiter ramp;

    public Shooter() {
        this(Map.map.getShooterConfig());
    }

    public Shooter(final Config _config) {
        config = _config;

        motor = SparkMaxFactory.createMaster(config.motorPort, MotorType.kBrushed);
        motor.setInverted(config.motorInvert);
        motor.setIdleMode(IdleMode.kCoast);

        // assuming 1:1
        encoder = new Encoder(config.encoderPortA, config.encoderPortB, config.encoderInvert,
                EncodingType.k4X);
        encoder.setDistancePerPulse(1 / config.encoderResolution);

        pid = new PIDController(config.kP, 0, 0);
        ramp = new SlewRateLimiter(1 / config.rampTime);
    }

    public final double getRPM() {
        return encoder.getRate() / 60;
    }

    public final void reset() {
        motor.setVoltage(0);
        ramp.reset(0);
        pid.reset();
    }

    public final void on() {
        final double desiredPercent = ramp.calculate(1);
        final double output = pid.calculate(getRPM(), config.speedRPM * desiredPercent)
                + (config.constantFF * desiredPercent);
        motor.setVoltage(Math.max(0, output));
    }

    public void off() {
        // No ramp down: just turn off power and go into coast
        // But still run calculations so can 'resume' ramp
        ramp.calculate(0);
        pid.calculate(getRPM(), 0);

        motor.setVoltage(0);
    }
}

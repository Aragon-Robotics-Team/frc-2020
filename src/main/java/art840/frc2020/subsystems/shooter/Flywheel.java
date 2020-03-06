package art840.frc2020.subsystems.shooter;

import art840.frc2020.map.Map;
import art840.frc2020.util.FileIO.CachedFile;
import art840.frc2020.util.hardware.SparkMaxFactory;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Flywheel extends SubsystemBase {
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

    ShuffleboardLayout motorTelemetry;
    double voltage;

    CachedFile file;
    Timer timer = new Timer();

    public Flywheel() {
        this(Map.map.shooter.flywheel);
    }

    public Flywheel(final Config _config) {
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

        motorTelemetry = Shuffleboard.getTab("Flywheel").getLayout("Motor", BuiltInLayouts.kList);
        motorTelemetry.addNumber("Voltage", () -> voltage);
        motorTelemetry.addNumber("Output", motor::get);
        motorTelemetry.addNumber("RPM", this::getRPM);
        motorTelemetry.addNumber("Error", () -> this.getRPM() - config.speedRPM);
    }

    public final double getRPM() {
        return encoder.getRate() * 60;
    }

    public final void setVoltage(double voltage) {
        this.voltage = voltage;
        motor.setVoltage(voltage);
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
        setVoltage(Math.max(0, output));
    }

    public final void off() {
        // No ramp down: just turn off power and go into coast
        // But still run calculations so can 'resume' ramp
        ramp.calculate(0);
        pid.calculate(getRPM(), 0);

        setVoltage(0);
    }

    public final boolean atDesiredSpeed() {
        return getRPM() > (config.speedRPM * 0.9);
    }

    public final Command spinAndWaitCommand() {
        return new RunCommand(this::on, this).withInterrupt(this::atDesiredSpeed);
    }

    public final void beginLogging() {
        file = new CachedFile("shooter.csv");
        file.println("Time,Voltage,Output,RPM,targetRPM=", config.speedRPM, ",rampTime=",
                config.rampTime, ",kP=", config.kP, ",constantFF=", config.constantFF);
        timer.reset();
        timer.start();
    }

    public final void endLogging() {
        timer.stop();
        if (file != null) {
            file.flush();
            file = null;
        }
    }

    public final void periodic() {
        if (file != null) {
            file.println(timer.get(), ",", voltage, ",", motor.get(), ",", getRPM());
        }
    }
}

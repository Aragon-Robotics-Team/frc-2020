package art840.frc2020.subsystems;

import static java.util.Objects.requireNonNull;

import art840.frc2020.map.Map;
import art840.frc2020.util.InstantCommandDisabled;
import art840.frc2020.util.TalonSRXWrapper;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

public class Turret extends SubsystemBase implements Loggable {
    public static class Config {
        public int motor;
        public boolean invert = false;

        public int quadratureResolution;
        public boolean invertEncoder;

        public double gearRatio = 1;

        public SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0, 1);
        public SlotConfiguration velocityPID = new SlotConfiguration();

        public double maxVelocity; // rps
        public double maxAcceleration;

        private final void verify() {
            requireNonNull(feedforward);
            requireNonNull(velocityPID);
        }
    }

    final Config config;
    final TalonSRXWrapper motor;

    @Log
    double pidOutput;
    @Log
    double ffOutput;
    @Log
    double netOutput;

    private final ProfiledPIDController pid;

    public Turret() {
        this(Map.map.getTurretConfig());
    }

    public Turret(final Config _config) {
        config = _config;
        config.verify();

        motor = new TalonSRXWrapper(new TalonSRXWrapper.Config() {
            {
                port = config.motor;
                invert = config.invert;
                invertEncoder = config.invertEncoder;
                encoderResolution = config.quadratureResolution;
                gearRatio = config.gearRatio;
            }
        });

        pid = new ProfiledPIDController(config.velocityPID.kP, config.velocityPID.kI,
                config.velocityPID.kD,
                new TrapezoidProfile.Constraints(config.maxVelocity, config.maxAcceleration));
    }

    @io.github.oblarg.oblog.annotations.Config
    final void pos(double val) {
        pos = val;
    }

    double pos;

    // public final void setPosition(double pos) {
    public final void periodic() {
        if (DriverStation.getInstance().isDisabled()) {
            reset();
            return;
        }

        pidOutput = pid.calculate(motor.getPosition(), pos);
        ffOutput = config.feedforward.calculate(pid.getSetpoint().velocity);
        netOutput = pidOutput + ffOutput;

        motor.setVoltage(netOutput);
    }

    @Log
    Command reset = new InstantCommandDisabled(this::reset, this);

    public final void reset() {
        pid.reset(motor.getPosition());
        motor.setVoltage(0);
    }
}

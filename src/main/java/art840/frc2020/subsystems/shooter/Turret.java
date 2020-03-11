package art840.frc2020.subsystems.shooter;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;

import art840.frc2020.map.Map;
import art840.frc2020.util.commands.InstantCommandDisabled;
import art840.frc2020.util.commands.RunEndCommand;
import art840.frc2020.util.hardware.TalonSRXWrapper;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

public class Turret extends SubsystemBase implements Loggable {
    public static class Config {
        public int motor = -1;
        public boolean invert = false;

        public int quadratureResolution;
        public boolean invertEncoder;
        
        public int leftHallEffect = -1;
        public int rightHallEffect = -1;

        public double gearRatio = 1;

        public SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0, 1);
        public SlotConfiguration velocityPID = new SlotConfiguration();

        public double maxVelocity; // rps
        public double maxAcceleration;

        public double kS;

        private final void verify() {
            requireNonNull(feedforward);
            requireNonNull(velocityPID);
        }
    }

    final Config config;
    final TalonSRXWrapper motor;
    
    final BooleanSupplier getLeftHallEffect;
    final BooleanSupplier getRightHallEffect;

    @Log
    double pidOutput;
    @Log
    double ffOutput;
    @Log
    double netOutput;

    private final ProfiledPIDController pid;

    public Turret() {
        this(Map.map.shooter.turret);
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
        pid.setTolerance(0.002);
        
        if (config.leftHallEffect >= 0) {
            getLeftHallEffect = (new DigitalInput(config.leftHallEffect))
        }
    }

    @io.github.oblarg.oblog.annotations.Config
    final void pos(double val) {
        pos = val;
    }
    
    
    double pos;
    // public final void setPosition(double pos) {
        DigitalInput heffect = new DigitalInput(9);
    public final void periodic() {
        boolean status = heffect.get();
        //SmartDashboard.putBoolean("DB/LED 9", heffect.get());
        System.out.println("Hall Effect:" + status);
        if (DriverStation.getInstance().isDisabled()) {
            reset();
            return;
        }
        
        
        
        /*
         *
         * Limelight readLimeLight = new Limelight();
         *
         * if (readLimeLight.tv == true) { if (readLimeLight.tx > 0) { pos = (readLimeLight.tx /
         * 29.8) * 0.4; } else if (readLimeLight.tx < 0) { pos = (readLimeLight.tx / 29.8) * 0.4; }
         * else { pos = 0; } }
         *
         * // pos = Robot.joystick.getThrottle() * 0.4;
         *
         * pidOutput = pid.calculate(motor.getPosition(), pos); if (pid.atSetpoint()) { pidOutput =
         * 0.; } ffOutput = config.feedforward.calculate(pid.getSetpoint().velocity); netOutput =
         * pidOutput + ffOutput;
         *
         * netOutput += Math.signum(netOutput) * config.kS;
         *
         * motor.setVoltage(netOutput);
         */
    }

    @Log
    Command reset = new InstantCommandDisabled(this::reset, this);

    public final void reset() {
        pid.reset(motor.getPosition());
        motor.setVoltage(0);
    }

    private final void _setPostion(double position) {
        pidOutput = pid.calculate(motor.getPosition(), position);
        ffOutput = config.feedforward.calculate(pid.getSetpoint().velocity);

        netOutput = pidOutput + ffOutput;

        netOutput += Math.signum(netOutput) * config.kS;

        motor.setVoltage(netOutput);
    }

    @Log
    boolean isDisabled = true;

    public final void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public final void setPosition(double position) {
        if (isDisabled) {
            return;
        }
        
        _setPostion(position);
    }

    public final Command moveToCenterAndDisable() {
        return new RunEndCommand(() -> setPosition(0), () -> {
            reset();
            setDisabled(true);
        }, pid::atGoal, this);
    }
}

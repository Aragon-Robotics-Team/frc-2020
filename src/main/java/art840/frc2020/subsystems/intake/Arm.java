package art840.frc2020.subsystems.intake;

import art840.frc2020.Robot;
import art840.frc2020.map.Map;
import art840.frc2020.util.hardware.SensorFactory;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Arm extends SubsystemBase {
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
    
    public Command centerIntakeAndArmIn() {
        return Robot.shooter.turret.moveToCenterAndDisable().andThen(() -> set(Position.In), this);
    }
    
    public Command armOut(){
        return new InstantCommand(() -> set(Position.Out), this)
        .andThen(new WaitCommand(2))
        .andThen(() -> Robot.shooter.turret.setDisabled(false), Robot.shooter.turret);
    }
}

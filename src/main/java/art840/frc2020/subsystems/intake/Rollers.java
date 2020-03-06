package art840.frc2020.subsystems.intake;

import art840.frc2020.map.Map;
import art840.frc2020.util.commands.RunEndCommand;
import art840.frc2020.util.hardware.SparkMaxFactory;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Rollers extends SubsystemBase {
    public static class Config {
        // Motor
        public int motor;

        public MotorType motorType = MotorType.kBrushless;

        // Controller
        public double voltsFull;
        public double rampTime;
    }

    public final class Motors {
        final CANSparkMax motor;

        private Motors() {
            motor = SparkMaxFactory.createMaster(config.motor, config.motorType);

            motor.setOpenLoopRampRate(config.rampTime);
            motor.setClosedLoopRampRate(config.rampTime);
        }

        public void setOn() {
            motor.setVoltage(config.voltsFull);
        }

        public void setZero() {
            motor.setVoltage(0);
        }
    }

    final Config config;
    public final Motors motors;

    public Rollers() {
        this(Map.map.intake.rollers);
    }

    public Rollers(final Config _config) {
        config = _config;

        motors = new Motors();
    }

    public Command setOnCommand() {
        return new RunEndCommand(motors::setOn, motors::setZero, this);
    }
}

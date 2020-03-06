package art840.frc2020.subsystems.hopper;

import art840.frc2020.map.Map;
import art840.frc2020.util.hardware.SparkMaxFactory;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Funnel extends SubsystemBase {
    public static class Config {
        // Motor
        public int leftMotor;
        public int rightMotor;

        public boolean invertAll = false;
        public boolean invertRight = true;

        public MotorType motorType = MotorType.kBrushless;

        // Controller
        public double voltsFull;
        public double voltsReverse;
        public double rampTime;
    }

    public final class Motors {
        final CANSparkMax leftMotor;
        final CANSparkMax rightMotor;

        final CANPIDController leftPID;
        final CANPIDController rightPID;

        private Motors() {
            leftMotor = SparkMaxFactory.createMaster(config.leftMotor, config.motorType);
            rightMotor = SparkMaxFactory.createMaster(config.rightMotor, config.motorType);

            leftMotor.setInverted(config.invertAll);
            rightMotor.setInverted(config.invertAll ^ config.invertRight);

            leftMotor.setOpenLoopRampRate(config.rampTime);
            leftMotor.setClosedLoopRampRate(config.rampTime);
            rightMotor.setOpenLoopRampRate(config.rampTime);
            rightMotor.setClosedLoopRampRate(config.rampTime);

            setBrake(false);

            leftPID = leftMotor.getPIDController();
            rightPID = rightMotor.getPIDController();
        }

        public void setBrake(boolean brake) {
            var mode = brake ? IdleMode.kBrake : IdleMode.kCoast;

            leftMotor.setIdleMode(mode);
            rightMotor.setIdleMode(mode);
        }

        public final void setVoltage(double left, double right) {
            leftPID.setReference(left, ControlType.kVoltage);
            rightPID.setReference(right, ControlType.kVoltage);
        }

        public final void setZero() {
            setVoltage(0, 0);
        }
    }

    final Config config;
    public final Motors motors;

    public Funnel() {
        this(Map.map.hopper.funnel);
    }

    public Funnel(final Config _config) {
        config = _config;

        motors = new Motors();
    }

    // We dont need PID, we just want it on, off, or unjamming (reverse)
    public final void setFwd() {
        motors.setVoltage(config.voltsFull, config.voltsFull);
    }

    public final void setReverse() {
        motors.setVoltage(-config.voltsReverse, config.voltsReverse);
    }

    public final void setOff() {
        motors.setZero();
    }
}

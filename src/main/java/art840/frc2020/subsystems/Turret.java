package art840.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.BooleanSupplier;

public class Turret extends SubsystemBase {
    final TalonSRX motor;

    double voltage;

    BooleanSupplier invert;

    public Turret() {
        motor = new TalonSRX(7);
        motor.clearStickyFaults();

        var motorconfig = new TalonSRXConfiguration();

        motorconfig.openloopRamp = 0.2;
        motorconfig.closedloopRamp = 0.2;
        motorconfig.voltageCompSaturation = 12;

        motor.configAllSettings(motorconfig);

        motor.setInverted(false);
        motor.setSensorPhase(false);
        motor.setNeutralMode(NeutralMode.Coast);
        motor.selectProfileSlot(0, 0);
        motor.enableVoltageCompensation(true);
        motor.setSelectedSensorPosition(0);

        var motorTelemetry = Shuffleboard.getTab("Turret").getLayout("Motor", BuiltInLayouts.kList);
        motorTelemetry.addNumber("Voltage", () -> voltage);
        motorTelemetry.addNumber("Output", motor::getMotorOutputPercent);
        motorTelemetry.addNumber("RPM", this::getRPM);
        motorTelemetry.addNumber("Position", this::getPosition);
        var entry = motorTelemetry.add("Invert", false).withWidget(BuiltInWidgets.kToggleSwitch)
                .getEntry();
        invert = () -> entry.getBoolean(false);
    }

    private final double toRotations(int val) {
        return (((double) val) // encoder ticks
                / 4096) // rotations of gearbox
                * 18 / 120; // sprocket reduction
    }

    public final double getPosition() {
        return toRotations(motor.getSelectedSensorPosition());
    }

    public final double getRPM() {
        return toRotations(motor.getSelectedSensorVelocity()) // rotations / 100ms
                * 10 // units / second
                * 60; // units / min
    }

    public final void setVoltage(double voltage) {
        this.voltage = voltage;
        motor.set(TalonSRXControlMode.PercentOutput, voltage / 12.0);
    }

    public final void reset() {
        setVoltage(0);
    }

    public final void on() {
        setVoltage(2 * (invert.getAsBoolean() ? -1 : 1));
    }

    public final void off() {
        setVoltage(0);
    }
}

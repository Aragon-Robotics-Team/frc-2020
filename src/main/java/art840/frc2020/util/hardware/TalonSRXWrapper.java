package art840.frc2020.util.hardware;

import art840.frc2020.util.Mock;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

public class TalonSRXWrapper implements Loggable {
    public static class Config extends TalonSRXConfiguration {
        public int port;
        public boolean invert;
        public boolean invertEncoder;
        public double encoderResolution;
        public double gearRatio = 1;

        public Config() {
            openloopRamp = 0;
            closedloopRamp = 0;
            nominalOutputForward = 0;
            nominalOutputReverse = 0;

            voltageCompSaturation = 12;
        }
    }

    static final int timeoutMs = 300;

    public final TalonSRX talon;
    final Config config;
    final double encoderConstant;

    @Log
    double voltage;

    public TalonSRXWrapper(final Config _config) {
        config = _config;
        encoderConstant = config.gearRatio / config.encoderResolution;

        if (config.port >= 0) {
            talon = new TalonSRX(config.port);
        } else {
            talon = Mock.mock(TalonSRX.class);
        }

        talon.clearStickyFaults(timeoutMs);
        talon.configAllSettings(config, timeoutMs);
        talon.setSensorPhase(config.invertEncoder);
        talon.setInverted(config.invert);

        setBrake(true);
        talon.selectProfileSlot(0, 0);
        talon.enableVoltageCompensation(true);
        talon.setSelectedSensorPosition(0, 0, timeoutMs);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, timeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, timeoutMs);
    }

    public final void setVoltage(double voltage) {
        this.voltage = voltage;
        talon.set(TalonSRXControlMode.PercentOutput, voltage / config.voltageCompSaturation);
    }

    public final void setBrake(boolean brake) {
        talon.setNeutralMode(brake ? NeutralMode.Brake : NeutralMode.Coast);
    }

    @Log
    public final double getOutput() {
        return talon.getMotorOutputPercent();
    }

    @Log
    public final double getPosition() {
        return ((double) talon.getSelectedSensorPosition()) * encoderConstant;
    }

    @Log
    public final double getRPS() {
        return ((double) talon.getSelectedSensorVelocity()) * 10 * encoderConstant;
    }
}

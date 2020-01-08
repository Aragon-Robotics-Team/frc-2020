package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.util.ColorUtils;
import java.util.Map;

public class ColorSensor {
    public final ColorSensorV3 color = new ColorSensorV3(I2C.Port.kOnboard);

    public final Notifier threadFast = new Notifier(this::periodicFast);
    public final Notifier threadSlow = new Notifier(this::periodicSlow);

    private static final ColorSensor instance = new ColorSensor();

    private SuppliedValueWidget<Boolean> widget;

    public static final ColorSensor getInstance() {
        return instance;
    }

    private ColorSensor() {
        threadFast.startPeriodic(0.1);
        threadSlow.startPeriodic(1);

        widget = Shuffleboard.getTab("Tab 1").addBoolean("flashing", ColorSensor::bruh);
        widget.withWidget(BuiltInWidgets.kBooleanBox).withPosition(0, 0).withSize(3, 3);
        // .withProperties(Map.of("Color when true", "#FFFFFF"));

        setColor(ColorUtils.toHexString(new Color8Bit(Color.kFuchsia)));
    }

    /*
     * public final boolean isConnected() { return color.m_simDevice != null; // private }
     */

    private final void setColor(String i) {
        // String i = ColorUtils.toHexString(c);
        widget.withProperties(Map.of("Color when false", i, "Color when true", i));
    }

    private static boolean bruh() {
        return (((int) Timer.getFPGATimestamp()) % 2 == 0);
        // return false;
    }

    private final void periodicFast() {
        // var c = color.getColor();

        // SmartDashboard.putNumber("Color/Red", c.red);
        // SmartDashboard.putNumber("Color/Green", c.green);
        // SmartDashboard.putNumber("Color/Blue", c.blue);

        // SmartDashboard.putBoolean("Color/Bruh", bruh());
    }

    private final void periodicSlow() {
        var c = ColorUtils.toHexString(ColorUtils.getBrightColor(color));

        System.out.println(c);
        setColor(c);
    }
}

package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import edu.wpi.first.wpilibj.util.Color;
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

        setColor(new java.awt.Color(100, 250, 0));
    }

    private final void setColor(java.awt.Color c) {
        // var i = c.getRGB();
        var i = toActualString(c);
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
        var c = color.getColor();

        System.out.println(toString(c));
        var C = toColor(c);
        setColor(C);
        System.out.println(toActualString(C));
    }

    public static final String toString(Color c) {
        return String.format("(%.4f, %.4f, %.4f)", c.red, c.green, c.blue);
    }

    public static final java.awt.Color toColor(Color c) {
        return new java.awt.Color((float) c.red, (float) c.green, (float) c.blue);
    }

    public static final String toActualString(java.awt.Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }
}

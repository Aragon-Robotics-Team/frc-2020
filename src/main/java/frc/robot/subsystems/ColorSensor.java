package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.ColorUtils;
import frc.robot.util.ShuffleboardRGB;

public class ColorSensor {
    public final ColorSensorV3 color = new ColorSensorV3(I2C.Port.kOnboard);

    public final Notifier threadFast = new Notifier(this::periodicFast);
    public final Notifier threadSlow = new Notifier(this::periodicSlow);

    private static final ColorSensor instance = new ColorSensor();

    public final ShuffleboardRGB widget =
            new ShuffleboardRGB(Shuffleboard.getTab("Drivetrain"), "Color Sensor");

    public static final ColorSensor getInstance() {
        return instance;
    }

    private ColorSensor() {
        threadFast.startPeriodic(0.1);
        // threadSlow.startPeriodic(1);
    }

    /*
     * public final boolean isConnected() { return color.m_simDevice != null; // private }
     */

    private final void periodicFast() {
        // var c = color.getColor();
        var c = ColorUtils.getBrightColor(color);
        var s = ColorUtils.toHexString(c);

        SmartDashboard.putString("Hex Color", s);
        // SmartDashboard.putString("Float Color", ColorUtils.toFloatString(c));
        widget.setColor(s);

        // SmartDashboard.putNumber("Color/Red", c.red);
        // SmartDashboard.putNumber("Color/Green", c.green);
        // SmartDashboard.putNumber("Color/Blue", c.blue);

        // SmartDashboard.putBoolean("Color/Bruh", bruh());
    }

    private final void periodicSlow() {
        // var c = ColorUtils.toHexString(ColorUtils.getBrightColor(color));

        // System.out.println(c);
        // setColor(c);
    }
}

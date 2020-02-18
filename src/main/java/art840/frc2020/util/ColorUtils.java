package art840.frc2020.util;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class ColorUtils {
    private ColorUtils() {}

    private static final String floatStringFmt = "(%.4f, %.4f, %.4f)";
    private static final String hexStringFmt = "#%02X%02X%02X";

    private static final float[] tempCol = new float[3];

    public static final String toFloatString(edu.wpi.first.wpilibj.util.Color c) {
        return String.format(floatStringFmt, c.red, c.green, c.blue);
    }

    public static final String toFloatString(java.awt.Color c) {
        c.getRGBColorComponents(tempCol);
        return String.format(floatStringFmt, tempCol[0], tempCol[1], tempCol[2]);
    }

    public static final String toHexString(edu.wpi.first.wpilibj.util.Color c) {
        // Float to 0-255 conversion from java.awt.Color impls
        return String.format(hexStringFmt, (int) (c.red * 255 + 0.5), (int) (c.green * 255 + 0.5),
                (int) (c.blue * 255 + 0.5));
    }

    public static final String toHexString(java.awt.Color c) {
        return String.format(hexStringFmt, c.getRed(), c.getGreen(), c.getBlue());
    }

    public static final String toHexString(Color8Bit c) {
        return String.format(hexStringFmt, c.red, c.green, c.blue);
    }

    public static final edu.wpi.first.wpilibj.util.Color getBrightColor(ColorSensorV3 sensor) {
        double red = sensor.getRed();
        double green = sensor.getGreen();
        double blue = sensor.getBlue();

        double max = Math.max(red, Math.max(green, blue));

        return new Color(red / max, green / max, blue / max);
    }
}

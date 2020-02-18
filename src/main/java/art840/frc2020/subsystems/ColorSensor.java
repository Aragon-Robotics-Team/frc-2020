package art840.frc2020.subsystems;

import art840.frc2020.util.ColorUtils;
import art840.frc2020.util.ShuffleboardRGB;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ColorSensor extends SubsystemBase {
    public final ColorSensorV3 color = new ColorSensorV3(I2C.Port.kOnboard);

    public Colors currentColor;
    String matchedColor;
    String matchedColorHex;
    double matchedConfidence;
    ShuffleboardRGB rawColor = new ShuffleboardRGB(Shuffleboard.getTab("TheTab"), "isIsAName");
    String rawColorHex;
    String rawColorFloat;

    public enum Colors {
        // Clockwise order
        Yellow(0, 0, 0), Blue(0, 0, 0), Red(0, 0, 0);

        public final Color color;
        public final String hex;

        public static final int size;
        private static final ColorMatch colorMatch = new ColorMatch();
        private static final Map<Color, Colors> conversionDict = new HashMap<Color, Colors>();

        private Colors(double r, double g, double b) {
            color = new Color(r, g, b);
            hex = ColorUtils.toHexString(color);
        }

        public static class MatchResult {
            public final Colors color;
            public final double confidence;

            public MatchResult(Color measured) {
                ColorMatchResult result = colorMatch.matchClosestColor(measured);
                color = conversionDict.get(result.color);
                confidence = result.confidence;

                if (color == null) {
                    throw new IllegalStateException("ColorMatch returned unbound color");
                }
            }
        }

        // This is required because you cannot access static fields in an enum constructor
        static {
            final var set = EnumSet.allOf(Colors.class);
            size = set.size();
            for (Colors color : set) {
                colorMatch.addColorMatch(color.color);
                conversionDict.put(color.color, color);
            }
        }

        public static MatchResult matchColor(Color measured) {
            return new MatchResult(measured);
        }
    }

    public ColorSensor() {}

    public final void periodic() {
        final Color measured;
        measured = color.getColor();
        // measured = ColorUtils.getBrightColor(color);

        rawColorHex = ColorUtils.toHexString(measured);
        rawColorFloat = ColorUtils.toFloatString(measured);

        final var result = Colors.matchColor(measured);
        currentColor = result.color;
        matchedConfidence = result.confidence;

        matchedColor = currentColor.toString();
        matchedColorHex = currentColor.hex;

        rawColor.setColor(rawColorHex);
    }

    public static boolean calculateDirection(Colors currentColor, Colors wantedColor) {
        // TODO: color offset - field sensor not same position as robot sensor
        // true = clockwise
        final int difference = wantedColor.ordinal() - currentColor.ordinal();
        final int cw = Math.floorMod(difference, Colors.size);
        final int ccw = Math.floorMod(-difference, Colors.size);
        final boolean direction = cw <= ccw;
        return direction;
    }
}

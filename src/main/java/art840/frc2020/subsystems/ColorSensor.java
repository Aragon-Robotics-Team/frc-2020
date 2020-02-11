package art840.frc2020.subsystems;

import art840.frc2020.util.ColorUtils;
import art840.frc2020.util.ShuffleboardRGB;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.util.Color;
import java.util.Map;

public class ColorSensor {
    public final ColorSensorV3 color = new ColorSensorV3(I2C.Port.kOnboard);

    public final Notifier threadFast = new Notifier(this::periodicFast);
    public final Notifier threadSlow = new Notifier(this::periodicSlow);

    private static final ColorSensor instance = new ColorSensor();

    public final ShuffleboardContainer tab = Shuffleboard.getTab("Color");

    public final ShuffleboardRGB widget = new ShuffleboardRGB(tab, "Color Sensor");
    public final NetworkTableEntry hexString =
            tab.add("Hex Color", "#000000").withWidget(BuiltInWidgets.kTextView).getEntry();
    public final NetworkTableEntry floatColor =
            tab.add("Float Color", "123").withWidget(BuiltInWidgets.kTextView).getEntry();
    public final NetworkTableEntry detectedColor =
            tab.add("Detected Color", "123").withWidget(BuiltInWidgets.kTextView).getEntry();
    public final NetworkTableEntry confidence =
            tab.add("Confidence", 123).withWidget(BuiltInWidgets.kTextView).getEntry();
    public final NetworkTableEntry colorNumGraph =
            tab.add("ColorNum", 0).withWidget(BuiltInWidgets.kGraph)
                    .withProperties(Map.of("Visible time", 5.0)).getEntry();

    public static final ColorSensor getInstance() {
        return instance;
    }

    private final static ColorMatch m_colorMatcher = new ColorMatch();

    // private final Color kBlueTarget = ColorMatch.makeColor(0.0879, 0.3247,
    // 0.5874);
    // private final Color kGreenTarget = ColorMatch.makeColor(0.2417, 0.5037,
    // 0.2549);
    // private final Color kRedTarget = ColorMatch.makeColor(0.4924, 0.3650,
    // 0.1426);

    public enum Colors {
        kBlue(ColorMatch.makeColor(0.0879, 0.3247, 0.5874)), kGreen(ColorMatch.makeColor(0.2417, 0.5037, 0.2549)),
        kRed(ColorMatch.makeColor(0.4924, 0.3650, 0.1426));

        public final Color color;

        private Colors(Color color) {
            this.color = color;
        }

        public static Colors nearestColors(Color obj) {
            m_colorMatcher.match(color);
        }
    }
    
    private ColorSensor() {
        threadFast.startPeriodic(0.1);
        m_colorMatcher.addColorMatch(Colors.kBlue.color);
        m_colorMatcher.addColorMatch(Colors.kGreen.color);
        m_colorMatcher.addColorMatch(Colors.kRed.color);
        // threadSlow.startPeriodic(1);
        // For cycling to desired color
    }

    /*public static String colorArray() {
        String[] desiredColor = {"RED", "BLUE", "GREEN", "YELLOW"};
        int i = 0;
        if (i < 3) {
            i += 1;
        } else {
            i = 0;
        }

        // System.out.println("Desired color: " + desiredColor[i]);
        return desiredColor[i];
    }*/

    
    //   public final boolean isConnected() { return color.m_simDevice != null; // private }
     

    private final void periodicFast() {
        var c = color.getColor();
        // var c = ColorUtils.getBrightColor(color);
        var s = ColorUtils.toHexString(c);

        hexString.setString(s);
        widget.setColor(s);
        floatColor.setString(ColorUtils.toFloatString(c));
        String colorString;
        double colorNum;
        ColorMatchResult match = m_colorMatcher.matchClosestColor(c);
        if (match.color == Colors.kBlue.color) {
            colorString = "Blue";
            colorNum = 1;
        } else if (match.color == Colors.kRed.color) {
            colorString = "Red";
            colorNum = 2;
        } else if (match.color == Colors.kGreen.color) {
            colorString = "Green";
            colorNum = 3;
        } else {
            colorString = "Unknown";
            colorNum = 0;
        }
        colorNum += Math.random() * 0.05;
        detectedColor.setString(colorString);
        colorNumGraph.setDouble(colorNum);
        confidence.setDouble(match.confidence);

        /*
         *
         * SmartDashboard.putString("Hex Color", s); // SmartDashboard.putString("Float Color",
         * ColorUtils.toFloatString(c)); widget.setColor(s);
         *
         * var p = (double) color.getProximity(); p = (1.0 - (p / 255.0)) * 10.0;
         * SmartDashboard.putNumber("Distance", p);
         *
         * // SmartDashboard.putNumber("Color/Red", c.red); //
         * SmartDashboard.putNumber("Color/Green", c.green); //
         * SmartDashboard.putNumber("Color/Blue", c.blue);
         *
         * // SmartDashboard.putBoolean("Color/Bruh", bruh());
         *
         */
    }

    private final void periodicSlow() {
        // var c = ColorUtils.toHexString(ColorUtils.getBrightColor(color));

        // System.out.println(c);
        // setColor(c);
    }
}

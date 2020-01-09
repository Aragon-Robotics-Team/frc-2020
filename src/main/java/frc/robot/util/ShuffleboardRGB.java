package frc.robot.util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import java.util.Map;

public class ShuffleboardRGB {
    public final SuppliedValueWidget<Boolean> widget;

    private final String offStr = "Color when false";
    private final String onStr = "Color when true";

    public ShuffleboardRGB(ShuffleboardContainer tab, String name) {
        widget = tab.addBoolean(name, this::booleanGet).withWidget(BuiltInWidgets.kBooleanBox);
        setColor("#000000");
    }

    // Impl 1

    public final void setColor(String color) {
        widget.withProperties(Map.of(offStr, color, onStr, color));
    }

    // Impl 2

    /*
     * private final Map<String, Object> map = new HashMap<String, Object>();
     *
     * public final void setColor(String color) { map.put(offStr, color); map.put(onStr, color);
     *
     * widget.withProperties(map); }
     */

    private final boolean booleanGet() {
        return (((int) Timer.getFPGATimestamp()) % 2 == 0);
        // return false;
    }
}

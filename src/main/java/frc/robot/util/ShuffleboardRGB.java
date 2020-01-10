package frc.robot.util;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import java.util.Map;

public class ShuffleboardRGB {
    public final SuppliedValueWidget<Boolean> widget;

    private static final String offStr = "Color when false";
    private static final String onStr = "Color when true";

    public ShuffleboardRGB(ShuffleboardContainer tab, String name) {
        widget = tab.addBoolean(name, () -> false).withWidget(BuiltInWidgets.kBooleanBox);
        setColor("#000000");
    }

    public final void setColor(String color) {
        widget.withProperties(Map.of(offStr, color, onStr, color));
    }
}

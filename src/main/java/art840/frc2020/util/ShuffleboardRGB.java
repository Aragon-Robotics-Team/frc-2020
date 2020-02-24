package art840.frc2020.util;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.ShuffleboardContainerWrapper;
import io.github.oblarg.oblog.SimpleWidgetWrapper;
import java.util.Map;

public class ShuffleboardRGB implements Loggable {
    private static final String offStr = "Color when false";
    private static final String onStr = "Color when true";

    private SimpleWidgetWrapper widget;

    private final String name;

    public ShuffleboardRGB(String _name) {
        name = _name;
    }

    public final boolean skipLayout() {
        return true;
    }

    public final void addCustomLogging(ShuffleboardContainerWrapper tab) {
        if (widget != null) {
            return;
        }

        widget = tab.add(name, false).withWidget(BuiltInWidgets.kBooleanBox.getWidgetName());
        setColor("#000000");
    }

    public final void setColor(String color) {
        if (widget != null) {
            widget.withProperties(getMap(color));
        }
    }

    private static final Map<String, Object> getMap(String color) {
        return Map.of(offStr, color, onStr, color);
    }
}

package frc.robot.oi;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.ArrayList;
import java.util.List;

public class OI implements Sendable {
    protected static final DriverStation ds = DriverStation.getInstance();

    public final int port;
    public final List<OI> children = new ArrayList<OI>(0);

    public OI(final int _port) {
        port = _port;
        setup();
    }

    protected void setup() {}

    protected final double getAxis(final int axis) {
        return ds.getStickAxis(port, axis);
    }

    protected final boolean isPressed(final int button) {
        return ds.getStickButton(port, button);
    }

    protected final Trigger getButton(final int button) {
        return new Trigger(() -> isPressed(button));
    }

    public OI addChild(OI child) {
        // Only to keep a reference so not GCed
        children.add(child);
        return this;
    }

    public final void initSendable(SendableBuilder builder) {}
}

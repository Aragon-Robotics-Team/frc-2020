package frc.robot.oi;

// TODO: finish
public class GenericController extends Joystick {
    // XBox controller, F310 controller, etc
    public GenericController(final int port) {
        super(port);
    }

    // Button Mappings
    // 1 - A
    // 2 - B
    // 3 - X
    // 4 - Y
    //

    public final double getThrottle() {
        return -getAxis(0);
    }

    public final double getTurn() {
        return getAxis(0);
    }

    public final double getThrottle2() {
        return -getAxis(0);
    }

    public final double getTurn2() {
        return getAxis(0);
    }

    public boolean disableCompressor() {
        return isPressed(0);
    }

    protected final void setup() {}
}

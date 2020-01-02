package frc.robot.oi;

// TODO: finish
public class GenericController extends Joystick {
    // XBox controller, F310 controller, etc

    private static final class Button {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;
    }

    private static final class Axis {
    }

    public GenericController(final int port) {
        super(port);
    }

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
        return isPressed(Button.A);
    }

    protected final void setup() {}
}

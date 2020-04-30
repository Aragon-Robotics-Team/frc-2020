package art840.frc2020.oi;

public class Attack3 extends Joystick {
    private static final class Button {
        public static final int Trigger = 1;
    }

    private static final class Axis {
        public static final int X = 0;
        public static final int Y = 1; // -
        public static final int Scroll = 2; // -
    }

    public Attack3(final int port) {
        super(port);
    }

    public final double getThrottle() {
        return -getAxis(Axis.Y);
    }

    public final double getTurn() {
        return getAxis(Axis.X);
    }

    public boolean disableCompressor() {
        return isPressed(8);
    }

    protected final void setup() {}
}

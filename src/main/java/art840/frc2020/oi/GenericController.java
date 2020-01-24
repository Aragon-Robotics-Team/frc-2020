package art840.frc2020.oi;

// TODO: finish
public class GenericController extends Joystick {
    // XBox controller, F310 controller, etc

    private static final class Button {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;
        public static final int LBump = 5;
        public static final int RBump = 6;
        public static final int MiddleL = 7;
        public static final int MiddleR = 8;
        public static final int LStickDown = 9;
        public static final int RStickDown = 10;
    }

    private static final class Axis {
        public static final int LX = 0;
        public static final int LY = 1; // -
        public static final int LT = 2;
        public static final int RT = 3;
        public static final int RX = 4;
        public static final int RY = 5; // -
    }

    public GenericController(final int port) {
        super(port);
    }

    private static final double applyDeadband(double val) {
        final double deadband = 0.07;

        if (Math.abs(val) < deadband) {
            return 0;
        } else if (val < 0) {
            return (val + deadband) / (1 - deadband);
        } else {
            return (val - deadband) / (1 - deadband);
        }
    }

    public final double getThrottle() {
        return applyDeadband(-getAxis(Axis.LY));
    }

    public final double getTurn() {
        return applyDeadband(getAxis(Axis.RX));
    }

    public final double getThrottle2() {
        return -getAxis(Axis.RY);
    }

    public final double getTurn2() {
        return getAxis(Axis.LX);
    }

    public boolean disableCompressor() {
        return isPressed(Button.A);
    }

    protected final void setup() {}
}

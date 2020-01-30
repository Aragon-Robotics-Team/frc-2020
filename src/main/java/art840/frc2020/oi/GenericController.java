package art840.frc2020.oi;

import art840.frc2020.Robot;
import art840.frc2020.subsystems.ColorSensor;
import art840.frc2020.util.ScalingUtils;

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

    static final double scale(double val) {
        return ScalingUtils.square(ScalingUtils.applyDeadband(val, 0.07));
    }

    public final double getThrottle() {
        return scale(-getAxis(Axis.LY));
    }

    public final double getTurn() {
        return scale(getAxis(Axis.RX));
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

    protected final void setup() {
        // getButton(Button.B).whenActive(() -> Robot.lift.sol.set(DoubleSolenoid.Value.kOff));
        // getButton(Button.X).whenActive(() -> Robot.lift.sol.set(DoubleSolenoid.Value.kForward));
        // getButton(Button.Y).whenActive(() -> Robot.lift.sol.set(DoubleSolenoid.Value.kReverse));

        // getButton(Button.X).whenActive(new
        // InstantCommandDisabled(FalconDashboard.instance::show));
        // getButton(Button.Y).whenActive(new
        // InstantCommandDisabled(FalconDashboard.instance::hide));

        getButton(Button.X).whenActive(() -> Robot.wheelSpinner.set(true), Robot.wheelSpinner);
        getButton(Button.Y).whenActive(() -> Robot.wheelSpinner.set(false), Robot.wheelSpinner);
        getButton(Button.A).whenActive(() -> Robot.wheelSpinner.stop(), Robot.wheelSpinner);
        getButton(Button.LBump).whenActive(() -> ColorSensor.colorArray());
    }
}

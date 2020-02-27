package art840.frc2020.util;

public class ScalingUtils {
    private ScalingUtils() {}

    static final double HALF_PI = Math.PI / 2;

    public static final double linear(final double x) {
        return x;
    }

    public static final double square(final double x) {
        return Math.copySign(x * x, x);
    }

    public static final double cube(final double x) {
        return x * x * x;
    }

    public static final double inverseSin(final double x) {
        return Math.asin(x) / HALF_PI;
    }

    public static final double applyDeadband(final double val, final double deadband) {
        if (Math.abs(val) < deadband) {
            return 0;
        } else if (val > 0) {
            return (val - deadband) / (1 - deadband);
        } else {
            return (val + deadband) / (1 - deadband);
        }
    }
}

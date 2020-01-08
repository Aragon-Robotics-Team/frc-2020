package art840.frc2020.util;

import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;

public class Tuple {
    public static final Tuple zero = new Tuple(); // no set pls

    public double left;
    public double right;

    public Tuple() {
        left = 0;
        right = 0;
    }

    public Tuple(double _left, double _right) {
        left = _left;
        right = _right;
    }

    public Tuple(Tuple tuple) {
        left = tuple.left;
        right = tuple.right;
    }

    public Tuple(DifferentialDriveWheelSpeeds speeds) {
        left = speeds.leftMetersPerSecond;
        right = speeds.rightMetersPerSecond;
    }

    public Tuple clear() {
        left = 0;
        right = 0;

        return this;
    }

    public Tuple set(double _left, double _right) {
        left = _left;
        right = _right;

        return this;
    }

    public Tuple set(Tuple tuple) {
        left = tuple.left;
        right = tuple.right;

        return this;
    }

    public Tuple set(DifferentialDriveWheelSpeeds speeds) {
        left = speeds.leftMetersPerSecond;
        right = speeds.rightMetersPerSecond;

        return this;
    }

    public DifferentialDriveWheelSpeeds get() {
        return new DifferentialDriveWheelSpeeds(left, right);
    }
}

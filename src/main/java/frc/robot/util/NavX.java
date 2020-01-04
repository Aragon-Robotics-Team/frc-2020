package frc.robot.util;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class NavX {
    private NavX() {}

    public static final byte updateRate = 100;
    public static final boolean invert = false; // TODO: ensure left turn is positive angle

    public static AHRS ahrs = new AHRS(SPI.Port.kMXP, updateRate);
    public static final double invertConst = invert ? -1 : 1;

    // getYaw() -180 - 180
    // getAngle() continuous
    public static Rotation2d getRotation() {
        return Rotation2d.fromDegrees(ahrs.getYaw() * invertConst);
    }

    public static double getRate() {
        return ahrs.getRate() * invertConst;
    }
}

package frc.robot.util;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class NavX {
    private NavX() {}

    public static AHRS ahrs = new AHRS(SPI.Port.kMXP, (byte)100);

    // getYaw() -180 - 180
    // getAngle() continuous
    // TODO: ensure left turn is positive angle, probably need negative

    public static Rotation2d getRotation() {
        return Rotation2d.fromDegrees(ahrs.getYaw());
    }
}

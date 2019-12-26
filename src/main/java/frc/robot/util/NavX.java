package frc.robot.util;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class NavX {
    public AHRS ahrs = new AHRS();

    // getYaw() -180 - 180
    // getAngle() continuous
    // TODO: ensure left turn is positive angle

    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(ahrs.getYaw());
    }
}

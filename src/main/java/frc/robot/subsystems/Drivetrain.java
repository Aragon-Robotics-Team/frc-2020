package frc.robot.subsystems;

import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.NavX;

public class Drivetrain extends SubsystemBase {
    NavX navx = new NavX();
    DifferentialDriveOdometry odometry;

    public Drivetrain() {
        odometry = new DifferentialDriveOdometry(navx.getRotation());
    }

    public void periodic() {
        odometry.update(navx.getRotation(), 0, 0);
    }
}

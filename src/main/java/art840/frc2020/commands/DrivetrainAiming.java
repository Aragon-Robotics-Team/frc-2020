package art840.frc2020.commands;

import art840.frc2020.Robot;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DrivetrainAiming extends CommandBase {
    public DrivetrainAiming() {
        addRequirements(Robot.drivetrain);
    }

    public void execute() {
        if (!Robot.sensors.limelight.tv) {
            return;
        }

        double Kp = 0.1; // don't know value yet
        double min_command = 0.05; // dont know value either
        // add minimum thing
        // double heading_error = tx;
    }

    public boolean isFinished() {
        return false;
    }

    public void end() {}
}

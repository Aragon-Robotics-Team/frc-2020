package art840.frc2020.commands;

import art840.frc2020.Robot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Shoot extends CommandBase {
    public static Command create() {
        return new SequentialCommandGroup(Robot.shooter.flywheel.spinAndWaitCommand(), new Shoot());
    }

    private Shoot() {
        addRequirements(Robot.shooter.flywheel, Robot.hopper.funnel, Robot.hopper.tower);
    }

    @Override
    public void execute() {
        Robot.shooter.flywheel.on();
        Robot.hopper.funnel.setFwd();
        Robot.hopper.tower.setFwd();
    }

    @Override
    public void end(boolean interrupted) {
        Robot.shooter.flywheel.off();
        Robot.hopper.funnel.setOff();
        Robot.hopper.tower.stop();
    }
}

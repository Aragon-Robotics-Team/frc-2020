package art840.frc2020.commands.drivetrain.auto;

import art840.frc2020.Robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class FollowTrajectory extends CommandBase {
    private final Timer timer = new Timer();
    private final Trajectory trajectory;

    public FollowTrajectory(Trajectory trajectory) {
        this.trajectory = trajectory;
        addRequirements(Robot.d);
    }

    @Override
    public void initialize() {
        Robot.d.director.setSavedVel(trajectory.sample(0));

        timer.reset();
        timer.start();
    }

    @Override
    public void execute() {
        Robot.d.director.driveRamsete(trajectory.sample(timer.get()));
    }

    @Override
    public void end(boolean interrupted) {
        timer.stop();
    }

    @Override
    public boolean isFinished() {
        return timer.hasPeriodPassed(trajectory.getTotalTimeSeconds());
    }
}

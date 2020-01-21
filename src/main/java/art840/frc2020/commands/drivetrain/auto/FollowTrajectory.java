package art840.frc2020.commands.drivetrain.auto;

import art840.frc2020.Robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class FollowTrajectory extends CommandBase {
    private final Timer timer = new Timer();
    private Trajectory trajectory;

    public FollowTrajectory(Trajectory trajectory) {
        this.trajectory = trajectory;
        addRequirements(Robot.d);
    }

    @Override
    public void initialize() {
        System.out.println("State0: " + trajectory.sample(0));
        this.trajectory = trajectory.relativeTo(trajectory.sample(0).poseMeters);

        var state = trajectory.sample(0);
        System.out.println("State1: " + state);

        Robot.d.odometry.resetToPose(state.poseMeters);
        Robot.d.auto.setSavedVel(state);

        timer.reset();
        timer.start();
    }

    @Override
    public void execute() {
        Robot.d.auto.driveRamsete(trajectory.sample(timer.get()));
    }

    @Override
    public void end(boolean interrupted) {
        timer.stop();
    }

    @Override
    public boolean isFinished() {
        return timer.hasPeriodPassed(5 + trajectory.getTotalTimeSeconds());
    }
}

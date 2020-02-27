package art840.frc2020.util;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import io.github.oblarg.oblog.Logger;

public class RobotBase extends TimedRobot {
    public void robotInit() {}

    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        Logger.updateEntries();
    }

    public void disabledInit() {}

    public void disabledPeriodic() {}

    public void autonomousInit() {}

    public void autonomousPeriodic() {}

    public void teleopInit() {}

    public void teleopPeriodic() {}

    public void testInit() {}

    public void testPeriodic() {}
}

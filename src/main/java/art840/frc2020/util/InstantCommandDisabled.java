package art840.frc2020.util;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class InstantCommandDisabled extends InstantCommand {
    public InstantCommandDisabled(Runnable toRun, Subsystem... requirements) {
        super(toRun, requirements);
    }

    public InstantCommandDisabled() {
        super();
    }

    public boolean runsWhenDisabled() {
        return true;
    }
}

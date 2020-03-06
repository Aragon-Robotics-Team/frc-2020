package art840.frc2020.util.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.function.BooleanSupplier;

public class RunEndCommand extends CommandBase {
    final Runnable run;
    final Runnable end;
    final BooleanSupplier finished;

    public RunEndCommand(Runnable run, Runnable end, BooleanSupplier finished,
            Subsystem... requirements) {
        this.run = run;
        this.end = end;
        this.finished = finished;
        addRequirements(requirements);
    }

    public RunEndCommand(Runnable run, Runnable end, Subsystem... requirements) {
        this(run, end, () -> false, requirements);
    }

    public RunEndCommand(Runnable run, BooleanSupplier finished, Subsystem... requirements) {
        this(run, () -> {
        }, finished, requirements);
    }

    public RunEndCommand(Runnable run, Subsystem... requirements) {
        this(run, () -> false, requirements);
    }

    @Override
    public void execute() {
        run.run();
    }

    @Override
    public void end(boolean interrupted) {
        end.run();
    }

    @Override
    public boolean isFinished() {
        return finished.getAsBoolean();
    }
}

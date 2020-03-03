package art840.frc2020.commands;

import art840.frc2020.Robot;
import art840.frc2020.subsystems.other.ColorSensor.Colors;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToColor extends CommandBase {
    Colors color;

    public RotateToColor(Colors _color) {
        color = _color;
        addRequirements(Robot.other.wheelSpinner);
    }

    public void execute() {
        var direction = Robot.other.colorSensor.calculateDirection(color);
        Robot.other.wheelSpinner.set(!direction);
    }

    public boolean isFinished() {
        return Robot.other.colorSensor.currentColor == color;
    }

    public void end(boolean interrupted) {
        Robot.other.wheelSpinner.stop();
    }
}

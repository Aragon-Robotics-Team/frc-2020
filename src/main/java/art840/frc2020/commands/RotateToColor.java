package art840.frc2020.commands;

import art840.frc2020.Robot;
import art840.frc2020.subsystems.ColorSensor.Colors;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToColor extends CommandBase {
    Colors color;

    public RotateToColor(Colors color) {
        Colors colors = this.color;
    }

    public void execute() {
        Colors current = Robot.color.getCurrentColor();
    }

    public Color booleanisFinished() {
        return Robot.color.getCurrentColor() == color;
    }

    public void end(boolean interrupted) {
        Robot.wheelSpinner.stop();
    }
}

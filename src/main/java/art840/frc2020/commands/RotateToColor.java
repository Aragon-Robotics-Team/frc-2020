package art840.frc2020.commands;

public class RotateToColor extends( CommandBase{
    Colors color;
    public RotateToColor(){
        this.color=colors;
    }
    
    public void execute(){
        Colors current = Robot.color.getCurrentColor();
    }
    
    public booleanisFinished() {
        return Robot.color.getCurrentColor() == color;
    }
    
    public void end(boolean interrupted){
        Robot.wheelSpinner.stop();
    }
}
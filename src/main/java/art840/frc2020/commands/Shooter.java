package art840.frc2020.commands;

import art840.frc2020.Robot;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Shooter extends CommandBase{
    
    public Shooter(){
        addRequirements(Robot.shooter.flywheel);
    }
    public void initialize(){
    }
    public void execute(){
        
    }
    
    public boolean isFinshed(){
        return false;
    }
    
    public void end(){
        
    }
}

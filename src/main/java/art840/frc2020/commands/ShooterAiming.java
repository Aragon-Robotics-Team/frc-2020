package art840.frc2020.commands;

import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShooterAiming extends CommandBase {
    public static NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    public static NetworkTableEntry tx = table.getEntry("tx");
    public static NetworkTableEntry ty = table.getEntry("ty");
    //public static double tx = table.GetNumber();
    
    public ShooterAiming() {
        this.tx = tx;
        this.ty = ty;
        this.table = table;
    }
    
    public void execute(){
        double Kp = 0.1; // don't know value yet
        double min_command = 0.05; // dont know value either    
        // add minimum thing 
        //double heading_error = tx;
        
    }
    
    public boolean isFinished(){
        return true;
    }
    
    public void end(){
        
    }
}

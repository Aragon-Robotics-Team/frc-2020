package art840.frc2020.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimeLight extends SubsystemBase{
    
    public static NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    public static NetworkTableEntry tx = table.getEntry("tx");
    public static NetworkTableEntry ty = table.getEntry("ty");
    // static NetworkTableEntry ta = table.getEntry("ta"); Add Distance Equation Thing

    public static void readValues() {
        double x = tx.getDouble(0.0);
        double y = ty.getDouble(0.0);
        // double area = ta.getDouble(0.0); 
        
        // SmartDashboard.putNumber("LimelightX", x); Not now...
        // SmartDashboard.putNumber("LimelightY", y);
        // SmartDashboard.putNumber("LimelightArea", area);
    }

}

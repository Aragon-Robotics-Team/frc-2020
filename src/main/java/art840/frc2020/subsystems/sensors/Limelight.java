package art840.frc2020.subsystems.sensors;

import art840.frc2020.subsystems.shooter.Hood;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Limelight extends SubsystemBase {
    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    private final NetworkTableEntry txEntry = table.getEntry("tx");
    private final NetworkTableEntry tyEntry = table.getEntry("ty");
    private final NetworkTableEntry tvEntry = table.getEntry("tv");

    public double tx;
    public double ty;
    public boolean tv;
    public double h1; // limelight height
    public double h2; // target height
    public double a1; // mounting angle
    
    public final void periodic() {
        tx = txEntry.getDouble(0);
        ty = tyEntry.getDouble(0);
        tv = tvEntry.getDouble(0) < 0.5;
    }
    
    public final double getDistance(){
        /*h1 = limelight height
        h2 = target height
        a1 = mounting angle
        */  
        double distance = (h2 - h1)/ Math.tan(a1 + ty);
        return distance;
    }
}

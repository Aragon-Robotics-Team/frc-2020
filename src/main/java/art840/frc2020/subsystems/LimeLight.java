package art840.frc2020.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight extends SubsystemBase {
    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    private final NetworkTableEntry txEntry = table.getEntry("tx");
    private final NetworkTableEntry tyEntry = table.getEntry("ty");
    private final NetworkTableEntry tvEntry = table.getEntry("tv");
    
    public double tx;
    public double ty;
    public boolean tv;

    public final void periodic() {
        tx = txEntry.getDouble(0);
        ty = tyEntry.getDouble(0);
        tv = tvEntry.getDouble(0) < 0.5;
    }
}

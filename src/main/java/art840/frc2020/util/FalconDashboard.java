package art840.frc2020.util;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.util.Units;

public class FalconDashboard {
    final NetworkTableEntry robotX;
    final NetworkTableEntry robotY;
    final NetworkTableEntry robotHeading;

    final NetworkTableEntry pathX;
    final NetworkTableEntry pathY;
    final NetworkTableEntry isFollowingPath;

    private final Tuple pos = new Tuple();

    public static final FalconDashboard instance = new FalconDashboard();

    private FalconDashboard() {
        // https://github.com/FRC5190/FalconDashboard#sending-data-to-falcon-dashboard

        var table = NetworkTableInstance.getDefault().getTable("/Live_Dashboard");

        robotX = table.getEntry("robotX");
        robotY = table.getEntry("robotY");
        robotHeading = table.getEntry("robotHeading");

        pathX = table.getEntry("pathX");
        pathY = table.getEntry("pathY");
        isFollowingPath = table.getEntry("isFollowingPath");
    }

    public void reset() {
        robotX.setDouble(0);
        robotY.setDouble(0);
        robotHeading.setDouble(0);
        pathX.setDouble(0);
        pathY.setDouble(0);
        isFollowingPath.setBoolean(true);
    }

    void convertPos(Pose2d pose) {
        // Assume pose in meters, (0, 0) in top-left
        // Need to convert to feet, (0, 0) in bottom-left
        // FalconDashboard calls top-left (0, 27)

        var translation = pose.getTranslation();

        pos.left = Units.metersToFeet(translation.getX());
        pos.right = 27 + Units.metersToFeet(translation.getY());
    }

    public void updateRobot(Pose2d pose) {
        convertPos(pose);

        robotX.setDouble(pos.left);
        robotY.setDouble(pos.right);

        robotHeading.setDouble(pose.getRotation().getRadians());
    }

    public void updatePath(Trajectory.State state) {
        updatePath(state.poseMeters);
    }

    public void updatePath(Pose2d pose) {
        convertPos(pose);

        pathX.setDouble(pos.left);
        pathY.setDouble(pos.right);
    }

    public void show() {
        isFollowingPath.setBoolean(true);
    }

    public void hide() {
        // Not accurate name, it just stops updating the drawing
        // The drawing is cleared when show() is called again
        isFollowingPath.setBoolean(false);
    }
}

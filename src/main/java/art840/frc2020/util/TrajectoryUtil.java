package art840.frc2020.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import java.io.IOException;
import java.nio.file.Path;

public class TrajectoryUtil {
    private TrajectoryUtil() {}

    public static final Path deployPath = Filesystem.getDeployDirectory().toPath();

    public static final Trajectory loadGeneratedPath(String name) {
        name = name + ".wpilib.json";

        Path path = deployPath.resolve(name);

        try {
            return edu.wpi.first.wpilibj.trajectory.TrajectoryUtil.fromPathweaverJson(path);
        } catch (IOException e) {
            var message = "Error while reading generated path: '" + name + "'";
            e.printStackTrace();
            throw loudExit(message);
        }
    }

    public static final RuntimeException loudExit(String message) {
        for (int i = 0; i < 2; i++) {
            DriverStation.reportError(message, false);
            Timer.delay(5);
        }
        return new RuntimeException(message);
    }
}

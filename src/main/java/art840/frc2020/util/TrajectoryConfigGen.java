package art840.frc2020.util;

import static java.util.Objects.requireNonNull;

import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveKinematicsConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint;
import java.util.ArrayList;
import java.util.List;

public class TrajectoryConfigGen {
    public static class Config {
        public double maxVelocity;
        public double maxAcceleration;
        public double maxVoltage;

        public SimpleMotorFeedforward feedforward;
        public SimpleMotorFeedforward feedforward2;
        public DifferentialDriveKinematics kinematics;

        public List<TrajectoryConstraint> constraints = new ArrayList<>();

        private final void verify() {
            requireNonNull(kinematics);

            if (constraints == null) {
                constraints = List.of();
            }
        }
    }

    final Config config;
    List<TrajectoryConstraint> constraints = new ArrayList<>();

    public TrajectoryConfigGen(Config _config) {
        config = _config;
        config.verify();

        addVoltageConstraint(config.feedforward);
        addVoltageConstraint(config.feedforward2);
        constraints.add(
                new DifferentialDriveKinematicsConstraint(config.kinematics, config.maxVelocity));
        constraints.addAll(config.constraints);
    }

    private final void addVoltageConstraint(SimpleMotorFeedforward feedforward) {
        if (feedforward == null) {
            return;
        }
        constraints.add(new DifferentialDriveVoltageConstraint(feedforward, config.kinematics,
                config.maxVoltage));
    }

    public final TrajectoryConfig generate() {
        return new TrajectoryConfig(config.maxVelocity, config.maxAcceleration)
                .addConstraints(constraints);
    }
}

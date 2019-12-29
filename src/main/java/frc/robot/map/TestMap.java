package frc.robot.map;

import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.subsystems.Drivetrain;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {{
            leftMotor = 1;
            rightMotor = 2;
            leftMotorSlave = 3;
            rightMotorSlave = 4;

            wheelCircumference = Units.inchesToMeters(6);
            trackWidth = Units.inchesToMeters(16);

            feedforward = new SimpleMotorFeedforward(0, 0, 0);
        }};
    }
}

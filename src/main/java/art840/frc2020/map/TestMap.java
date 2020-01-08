package art840.frc2020.map;

import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.util.Units;
import art840.frc2020.oi.GenericController;
import art840.frc2020.oi.Joystick;
import art840.frc2020.oi.OI;
import art840.frc2020.subsystems.Drivetrain;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 1;
                rightMotor = 2;
                leftMotorSlave = 3;
                rightMotorSlave = 4;

                wheelCircumference = Units.inchesToMeters(6);
                trackWidth = Units.inchesToMeters(16);

                feedforward = new SimpleMotorFeedforward(0, 0, 0);

                velocityPID = new SlotConfiguration();
                velocityPID.kP = 1.0;

                maxVelocity = 10; // m/s
                maxRotation = 1; // rad/s
            }
        };
    }

    public Joystick getJoystick() {
        return (new GenericController(0)).addChild(new OI(1));
    }
}

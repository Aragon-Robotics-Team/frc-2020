package art840.frc2020.map;

import art840.frc2020.oi.GenericController;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.util.Units;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 2;
                rightMotor = 5;

                leftMotorSlave = -1;
                rightMotorSlave = -1;

                invertAll = true;

                motorType = MotorType.kBrushed;
                quadratureResolution = 2048 * 4;

                gearRatio = 1;
                wheelCircumference = Units.inchesToMeters(6.0 * Math.PI);
                trackWidth = Units.inchesToMeters(24.0);

                feedforwardLeft = new SimpleMotorFeedforward(1.45, 2.41, 0.885);
                feedforwardRight = new SimpleMotorFeedforward(1.45, 2.41, 0.885);
                // velocityPID.kP = 0.2;

                maxVelocity = 3; // m/s
                maxRotation = 2 * Math.PI; // rad/s
            }
        };
    }

    public Joystick getJoystick() {
        return new GenericController(0);
    }
}

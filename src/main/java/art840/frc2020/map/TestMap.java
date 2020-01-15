package art840.frc2020.map;

import art840.frc2020.oi.GenericController;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.util.Units;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 2;
                rightMotor = 5;

                leftMotorSlave = -1;
                rightMotorSlave = -1;

                motorType = MotorType.kBrushed;
                quadratureResolution = 2048 * 4;

                gearRatio = 1;
                wheelCircumference = Units.inchesToMeters(6.0 * Math.PI);
                trackWidth = Units.inchesToMeters(24.0);
            }
        };
    }

    public Joystick getJoystick() {
        return new GenericController(0);
    }
}

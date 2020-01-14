package art840.frc2020.map;

import art840.frc2020.oi.Attack3;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 2;
                rightMotor = 5;

                motorType = MotorType.kBrushed;
                quadratureResolution = 2048 * 4;
            }
        };
    }

    public Joystick getJoystick() {
        return new Attack3(0);
    }
}

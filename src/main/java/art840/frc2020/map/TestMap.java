package art840.frc2020.map;

import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 1;
                rightMotor = 2;
                leftMotorSlave = 3;
                rightMotorSlave = 4;

                motorType = MotorType.kBrushed;
                quadratureResolution = 2048 * 4;
            }
        };
    }

    public Joystick getJoystick() {
        return new Joystick();
    }
}

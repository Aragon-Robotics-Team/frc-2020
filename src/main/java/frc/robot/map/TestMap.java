package frc.robot.map;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.oi.Joystick;
import frc.robot.subsystems.Drivetrain;

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

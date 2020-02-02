package art840.frc2020.map;

import art840.frc2020.oi.GenericController;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.Lift;
import art840.frc2020.subsystems.WheelSpinner;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.util.Units;

public class TestMap extends Map {
    public Drivetrain.Config getDrivetrainConfig() {
        return new Drivetrain.Config() {
            {
                leftMotor = 2;
                rightMotor = 5;

                leftMotorSlave = 1;
                rightMotorSlave = 3;

                invertAll = true;

                motorType = MotorType.kBrushed;
                quadratureResolution = 2048 * 4;

                gearRatio = 1;
                wheelCircumference = Units.inchesToMeters(6.0 * Math.PI);
                trackWidth = Units.inchesToMeters(24.0);

                // feedforwardCombined = new SimpleMotorFeedforward(0.87, 2.29, 0.617);
                feedforwardLeft = new SimpleMotorFeedforward(0.857, 2.3, 0.611);
                feedforwardRight = new SimpleMotorFeedforward(0.883, 2.28, 0.622);
                velocityPID.kP = 0.1;

                maxVelocity = 3; // m/s
                maxAcceleration = 6;
                maxAngularVelocity = 2 * Math.PI; // rad/s
                maxAngularAccel = 4 * Math.PI;

                teleopLinearSlew = 6;
                teleopRotationalSlew = 4 * Math.PI;
            }
        };
    }

    public Joystick getJoystick() {
        return new GenericController(0);
    }

    public int getPCMId() {
        return 1;
    }

    public Lift.Config getLiftConfig() {
        return new Lift.Config() {
            {
                solenoidFwd = 1;
                solenoidRev = 0;
            }
        };
    }

    public WheelSpinner.Config getWheelSpinnerConfig() {
        return new WheelSpinner.Config() {
            {
                motorControllerPort = 3;
                maxSpeed = 0.5;
                rampTime = 0.5;
            }
        };
    }
}

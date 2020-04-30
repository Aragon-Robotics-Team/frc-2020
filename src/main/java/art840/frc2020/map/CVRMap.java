package art840.frc2020.map;

import art840.frc2020.oi.GenericController;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.hopper.Funnel;
import art840.frc2020.subsystems.hopper.Tower;
import art840.frc2020.subsystems.intake.Arm;
import art840.frc2020.subsystems.intake.Rollers;
import art840.frc2020.subsystems.other.Climb;
import art840.frc2020.subsystems.shooter.Flywheel;
import art840.frc2020.subsystems.shooter.Hood;
import art840.frc2020.subsystems.shooter.Turret;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.util.Units;

public class CVRMap extends Map {
    public CVRMap() {
        joystick = new GenericController(0);

        drivetrain = new Drivetrain.Config() {
            {
                leftMotor = 8;
                leftMotorSlave = 1;

                rightMotor = 2;
                // rightMotorSlave = 3;
                rightMotorSlave = 5;

                this.invertAll = true;
                motorType = MotorType.kBrushless;

                gearRatio = 1.0 / 15.0;
                wheelCircumference = Units.inchesToMeters(6.0 * Math.PI);
                trackWidth = Units.inchesToMeters(24.75);

                maxVelocity = 8; // m/s
                maxAcceleration = 6;
                maxAngularVelocity = 8 * Math.PI; // rad/s
                // maxAngularVelocity = 8 * Math.PI; // rad/s
                maxAngularAccel = 4 * Math.PI;

                teleopLinearSlew = 32;
                teleopRotationalSlew = 32 * Math.PI;
            }
        };

        hopper.funnel = new Funnel.Config() {
            {
                leftMotor = 7;
                rightMotor = 6;

                invertRight = false;
                motorType = MotorType.kBrushless;

                voltsFull = 2;
                voltsReverse = 2;
                rampTime = 1;
            }
        };

        hopper.tower = new Tower.Config() {
            {
                motorPort = 1;

                voltsFull = 12;
                voltsReverse = 6;
                rampTime = 1;
            }
        };

        intake.rollers = new Rollers.Config() {
            {
                motor = 4;
                invert = true;

                voltsFull = 12;
                rampTime = 1;
            }
        };

        shooter.flywheel = new Flywheel.Config() {
            {
                motorPort = 5;
                motorPortSlave = 7;

                invertAll = false;
                invertSlave = true;

                encoderInvert = false;
                encoderResolution = 4096;
                gearRatio = 1.0 / 3.0;

                rampTime = 1;

                constantFF = 12;
            }
        };

        shooter.turret = new Turret.Config() {
            {
                motor = 4;
            }
        };

        other.climb = new Climb.Config() {
            {
                solenoidLeftFwd = 5;
                solenoidLeftRev = 2;

                solenoidRightFwd = 4;
                solenoidRightRev = 3;

                disabled = false;
            }
        };

        intake.arm = new Arm.Config() {
            {
                solenoidLeftFwd = 0;
                solenoidLeftRev = 7;

                solenoidRightFwd = 1;
                solenoidRightRev = 6;

                disabled = false;
            }
        };

        shooter.hood = new Hood.Config() {
            {
                disabled = true;
            }
        };

        other.pcmId = 2;
    }
}

package art840.frc2020;

import art840.frc2020.map.Map;
import art840.frc2020.oi.Joystick;
import art840.frc2020.subsystems.Drivetrain;
import art840.frc2020.subsystems.hopper.Hopper;
import art840.frc2020.subsystems.intake.Intake;
import art840.frc2020.subsystems.other.Other;
import art840.frc2020.subsystems.sensors.Sensors;
import art840.frc2020.subsystems.shooter.Shooter;
import art840.frc2020.util.RobotBase;
import art840.frc2020.util.commands.InstantCommandDisabled;
import art840.frc2020.util.hardware.NavX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import io.github.oblarg.oblog.Logger;

public class Robot extends RobotBase {
    public static Drivetrain drivetrain = new Drivetrain();
    public static Hopper hopper = new Hopper();
    public static Intake intake = new Intake();
    public static Other other = new Other();
    public static Sensors sensors = new Sensors();
    public static Shooter shooter = new Shooter();
    public static Joystick joystick = Map.map.joystick;

    Command waitAndCoast = new WaitCommand(5)
            .andThen(new InstantCommandDisabled(() -> drivetrain.setBrake(false)));

    @Override
    public void robotInit() {
        joystick._setup();

        // addAuto("Test");
        // addAuto("ASDF");
        // addAuto("FWD");
        // addAuto("LFWD");
        // addAuto("RFWD");
        // Shuffleboardrivetrain.getTab("Drivetrain").add(c);

        NavX.ahrs.reset();
        drivetrain.odometry.resetAll();

        Logger.configureLoggingAndConfig(this, false);
    }

    @Override
    public void teleopInit() {
        shooter.flywheel.reset();
        // shooter.flywheel.beginLogging();
        // (new RunCommand(shooter.flywheel::on, shooter.flywheel)).schedule();

        waitAndCoast.cancel();
        drivetrain.setBrake(true);

        drivetrain.odometry.resetAll();
        drivetrain.controller.reset();

        drivetrain.teleop.driveArcade().schedule();

        intake.arm.armOut().schedule();

        // (new RunEndCommand(() -> drivetrain.controller.driveVoltage(new Tuple(4, 4)),
        // drivetrain.controller::driveZero, drivetrain)).schedule();

        // (new RunEndCommand(intake.rollers.motors::setOn, intake.rollers.motors::setZero,
        // intake.rollers)).schedule();

        // (new RunEndCommand(hopper.funnel::setFwd, hopper.funnel::setOff,
        // hopper.funnel)).schedule();

        // (new RunEndCommand(hopper.tower::setFwd, hopper.tower::stop, hopper.tower)).schedule();

        // (new RunEndCommand(shooter.flywheel::on, shooter.flywheel::off, shooter.flywheel))
        // .schedule();

        // drivetrain.teleop.driveArcade().beforeStarting(drivetrain.controller::driveZero).schedule();

        // Tuple vel = new Tuple(3, 3);
        // Tuple ff = new Tuple(7.5, 7.5);

        // (new WaitCommand(1))
        // .andThen(new RunCommand(() -> drivetrain.controller._driveRawVelocity(vel, ff), d))
        // .schedule();
    }

    @Override
    public void disabledInit() {
        waitAndCoast.schedule();
        shooter.flywheel.reset();
        // shooter.flywheel.endLogging();
    }

    @Override
    public void teleopPeriodic() {
        // NetworkTableInstance.getDefault().flush();
        // shooter.on();
    }

    @Override
    public void autonomousInit() {
        waitAndCoast.cancel();
        drivetrain.setBrake(true);

        NavX.ahrs.reset();
        drivetrain.controller.reset();
        drivetrain.odometry.resetAll();

        intake.arm.armOut().schedule();

        // (c.getSelected()).schedule();
        // autoCommandrivetrain.schedule();
        // (new WaitCommand(1))
        // .andThen(new RunCommand(
        // () -> drivetrain.controller._driveRawVelocity(Tuple.zero, Tuple.zero), d))
        // .schedule();
        // (new RunCommand((() -> drivetrain.controller._driveVoltage(Tuple.zero)), d)).schedule();
    }
}

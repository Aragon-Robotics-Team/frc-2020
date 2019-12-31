package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class Main {
    private Main() {}

    public static final void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}

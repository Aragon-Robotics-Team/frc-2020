package frc.robot.oi;

public class Joystick extends OI {
    public Joystick(final int port) {
        super(port);
    }

    public Joystick() {
        super();
    }

    public double getThrottle() {
        return 0;
    }

    public double getTurn() {
        return 0;
    }

    public double getThrottle2() {
        return 0;
    }

    public double getTurn2() {
        return 0;
    }

    public boolean disableCompressor() {
        return false;
    }

    public Joystick addChild(OI child) {
        // Only to keep a reference so not GCed
        children.add(child);
        return this;
    }
}

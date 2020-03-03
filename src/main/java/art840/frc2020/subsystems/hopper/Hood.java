package art840.frc2020.subsystems.hopper;

import art840.frc2020.map.Map;

public class Hood {
    public static class Config {
    }

    public static enum Position {
        In, Out;
    }

    final Config config;
    // Todo: determine solenoid setup
    private Position position;

    public Hood() {
        this(Map.map.hopper.hood);
    }

    public Hood(Config _config) {
        config = _config;

        set(Position.In);
    }

    private void _set() {}

    public void set(Position _position) {
        position = _position;

        switch (position) {
            case In:
                _set();
                break;
            case Out:
                _set();
                break;
        }
    }
}

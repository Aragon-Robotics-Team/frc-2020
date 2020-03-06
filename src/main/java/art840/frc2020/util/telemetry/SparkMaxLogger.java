package art840.frc2020.util.telemetry;

import com.revrobotics.CANSparkMax;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

public class SparkMaxLogger implements Loggable {
    @Log(methodName = "get")
    final CANSparkMax spark;

    @Log
    double voltage;

    public SparkMaxLogger(final CANSparkMax _spark) {
        spark = _spark;
    }
}

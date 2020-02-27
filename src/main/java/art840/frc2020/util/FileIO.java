package art840.frc2020.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileIO {
    public static final File root = new File(Filesystem.getOperatingDirectory(), "server");
    public static final String rootPath = root.getAbsolutePath();

    static {
        root.mkdirs();
        write("test.txt", "Working!");
    }

    public static final Path getPath(String name) {
        return (new File(root, name)).toPath();
    }

    // Will overwrite
    public static final void write(String name, String value) {
        try {
            Files.writeString(getPath(name), value);
        } catch (IOException e) {
            DriverStation.reportError("Could not write to file: " + name, true);
        }
    }

    public static class CachedFile {
        public final String name;
        public final StringBuilder builder = new StringBuilder();

        public CachedFile(String _name) {
            name = _name;
        }

        public final void println(Object... vals) {
            for (Object obj : vals) {
                builder.append(obj.toString());
            }
            builder.append("\n");
        }

        public final void flush() {
            write(name, builder.toString());
        }
    }
}

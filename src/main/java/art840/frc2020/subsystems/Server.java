package art840.frc2020.subsystems;

import art840.frc2020.util.FileIO;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Server {
    final Javalin app;

    public Server() {
        app = Javalin.create(config -> {
            config.addStaticFiles(FileIO.rootPath, Location.EXTERNAL);
        }).start(5800);
        app.get("/hello", ctx -> ctx.result("Hello, World!"));
    }
}

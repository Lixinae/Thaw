package fr.umlv.thaw.logger;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ThawLogger {

    private final Logger thawLogger = Logger.getLogger("ThawLogger");
    private final boolean enabled;

    public ThawLogger(boolean enabled) throws IOException {
        String fileName = Objects.requireNonNull(setNameWithCurrentDate());
        FileHandler fileHandler = new FileHandler(fileName, true);
        Logger l = Logger.getLogger("");
        fileHandler.setFormatter(new SimpleFormatter());
        l.addHandler(fileHandler);
        l.setLevel(Level.CONFIG);
        this.enabled = enabled;
    }

    private String setNameWithCurrentDate() {
        return "./logs/log_" + Date.from(Instant.now()).toString().replaceAll(" ", "__").replaceAll(":", "_") + ".log";
    }

    // basic logging
    public void log(Level level, String message) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(level);
        if (enabled) {
            thawLogger.log(level, message);
        }
    }
}

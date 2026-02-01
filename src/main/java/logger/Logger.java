package logger;

import javax.management.RuntimeErrorException;

public class Logger {

    private static Logger instance;
    private LogAppender appender = new FileLogAppender("logs/app.log");

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }

        return instance;
    }

    public void setLogAppender(LogAppender appender) {
        this.appender = appender;
    }

    private void log(LogLevel level, String origin, String message) {
        this.appender.write(new LogEntry(level, origin, message));
    }

    public void info(String origin, String message) {
        this.log(LogLevel.INFO, origin, message);
    }

    public void warning(String origin, String message) {
        this.log(LogLevel.WARNING, origin, message);
    }

    public void error(String origin, String message) {
        this.log(LogLevel.ERROR, origin, message);
        throw new RuntimeException("\"" + message + "\"" + " in: " + origin);
    }
}
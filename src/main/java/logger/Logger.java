package logger;

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

    public void log(LogLevel level, String origin, String message) {
        this.appender.write(new LogEntry(level, origin, message));
    }
}
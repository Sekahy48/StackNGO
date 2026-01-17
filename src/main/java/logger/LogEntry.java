package logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {

    private LogLevel level;
    private String origin;
    private String message;
    private LocalDateTime timeStamp;

    public LogEntry(LogLevel level, String origin, String message) {
        this.timeStamp = LocalDateTime.now();
        this.level = level;
        this.origin = origin;
        this.message = message;
    }

    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = timeStamp.format(formatter);

        StringBuilder str = new StringBuilder();

        str.append("(" + formattedTime + ")" + "\t");
        str.append("[" + this.level + "] ");
        str.append(this.origin + ": " + this.message);

        return str.toString();
    }   
}

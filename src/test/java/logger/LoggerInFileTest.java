package logger;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoggerInFileTest {

    private static final String LOG_PATH = "logs/test/test.log";
    private LogAppender appender;

    @BeforeEach
    public void setUp() {
        appender = new FileLogAppender(LOG_PATH);

        if (appender instanceof FileLogAppender) {
            ((FileLogAppender) appender).clear();
        }
    }

    @Test
    public void testFileLogAppender() throws IOException {

         
        File file = new File(LOG_PATH);
        File parent = file.getParentFile();

        assertNotNull(parent);
        assertTrue(parent.exists());
        assertTrue(parent.isDirectory());

        
        Logger logger = Logger.getInstance();
        logger.setLogAppender(appender);

        logger.log(LogLevel.INFO, "LoggerTest", "Info");
        logger.log(LogLevel.WARNING, "LoggerTest", "Warning");
        logger.log(LogLevel.ERROR, "LoggerTest", "Error");

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(3, lines.size());
 
        ((FileLogAppender) appender).clear();

        List<String> clearedLines = Files.readAllLines(file.toPath());
        assertEquals(0, clearedLines.size());
    }
}

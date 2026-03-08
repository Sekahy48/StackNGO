package logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoggerInShellTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testWritePrintsLogEntry() {
        ShellLogAppender appender = new ShellLogAppender();
        LogEntry entry = new LogEntry(
                LogLevel.INFO,
                "ShellLogAppenderTest",
                "Hola consola"
        );

        appender.write(entry);

        String output = outContent.toString().trim();
        assertEquals(entry.toString(), output); 

    }

    @Test
    public void testLogPerSe(){
        System.setOut(originalOut);
        Logger.getInstance().setLogAppender(new ShellLogAppender());
        Logger.getInstance().info(this.getClass().toString(), "El log de shell funciona bien");
    
    }
}

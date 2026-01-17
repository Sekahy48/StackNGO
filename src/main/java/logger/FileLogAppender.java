package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogAppender implements LogAppender, ClearableAppender {

    private File file;

    public FileLogAppender(String filePath) {
    this.file = new File(filePath);

    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
        parent.mkdirs();
    }
}

    @Override
    public void write(LogEntry entry) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry.toString());  
            writer.newLine();  
        } catch (IOException e) { 
        }
    }

    @Override
    public void clear() {
        try {
            // Sobrescribe el archivo con nada
            new FileWriter(file, false).close();
        } catch (IOException e) { 
        }
    }

}
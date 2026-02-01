package utilities;

import javafx.scene.image.Image;

import java.io.File;

public class ImageUtils {

    public static Image getImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        if (path.startsWith("file:")){
            return new Image(path);
        } else{
            File file = new File(path);
            return new Image(file.toURI().toString());
        }
    }

}

package mvc.model.entries;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import identificators.EntryId;

/**
 * Abstract class representing a general entry with common attributes.
 */
public abstract class Entry implements Comparable<Entry>{ 
    protected String name;
    protected String description;
    protected String imagePath;
    protected EntryId id;

    //#region Constructors
    public Entry(String name, String description, String imagePath, int id) {
        if(name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        this.description = description;
        this.setImagePath(imagePath);
        this.id = new EntryId(id);
    }

    public Entry(String name, int id) {
        if(name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        this.id = new EntryId(id);
    }

    //#endregion

    //#region Getters and Setters 
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getImagePath() {
        return imagePath;
    }
    public EntryId getId() {
        return id;
    }

    public void setName(String name) {
        if(name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String originalPath) {
        if (originalPath == null) {
            this.imagePath = null;
            return;
        }

        try {
            // --- Normaliza path si viene en formato URI ---
            String pathToUse = originalPath.startsWith("file:/") ? Paths.get(URI.create(originalPath)).toString() : originalPath;
            Path src = Paths.get(pathToUse);

            // --- Carpeta de imágenes de la app ---
            Path appImagesDir = Paths.get(System.getProperty("user.home"), "Stack&Go", "images");
            Files.createDirectories(appImagesDir);

            Path dest;
            // Si la imagen ya está en la carpeta de la app, no hacemos nada
            if (src.startsWith(appImagesDir)) {
                dest = src;
            } else {
                dest = appImagesDir.resolve(src.getFileName());
                Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            this.imagePath = dest.toString();

        } catch (IOException e) {
            e.printStackTrace();
            this.imagePath = originalPath;
        }
    }


    public void setId(int id) {
        this.id = new EntryId(id);
    }
    //#endregion

    //#region Comparable Implementation
    @Override
    public int compareTo(Entry other) {
        return this.id.compareTo(other.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry other = (Entry) o;
        return id.equals(other.id);
    }
    ////#endregion
    
    @Override
    public String toString(){
        return this.name + ": " + this.description;
    }

    public String getRelativeImagePath() {
    Path home = Paths.get(System.getProperty("user.home"), ".myapp");
    return home.relativize(Paths.get(this.imagePath)).toString();
}

}
    


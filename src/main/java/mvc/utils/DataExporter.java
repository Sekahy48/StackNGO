package mvc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import javafx.stage.FileChooser;
import mvc.context.RuntimeContext;

public class DataExporter {

    private final RuntimeContext context;

    public DataExporter(RuntimeContext context) {
        this.context = context;
    }

    public void exportUserData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar datos del usuario");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP files", "*.zip")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {

            List<Map<String, Object>> collectionsData = new ArrayList<>();
            Set<Path> imagesToAdd = new HashSet<>();

            for (CollectionDTO c : context.getCollections()) {

                Map<String, Object> collMap = new LinkedHashMap<>();

                // ===== COLLECTION =====
                String collIcon = null;
                if (c.iconPath != null) {
                    Path p = Paths.get(c.iconPath);
                    imagesToAdd.add(p);
                    collIcon = "images/" + p.getFileName();
                }


                CollectionDTO exportedCollection = DTOFactory.collection(
                        null,
                        null,
                        c.name,
                        collIcon,
                        c.description,
                        c.id
                );

                collMap.put("collection", exportedCollection);

                // ===== ITEMS =====
                List<ItemDTO> items =
                        context.getItemsAsEntriesByCollection(new identificators.EntryId(c.id));

                List<ItemDTO> exportedItems = new ArrayList<>();

                for (ItemDTO i : items) {
                    String iconPath = null;

                    if (i.iconPath != null) {
                        Path p = Paths.get(i.iconPath);
                        imagesToAdd.add(p);
                        iconPath = "images/" + p.getFileName();
                    }

                    exportedItems.add(DTOFactory.item(
                            i.name,
                            iconPath,
                            i.description,
                            i.id
                    ));
                }

                collMap.put("items", exportedItems);

                // ===== RECIPES =====
                List<RecipeDTO> recipes =
                        context.getRecipesAsEntriesByCollection(new identificators.EntryId(c.id));

                List<RecipeDTO> exportedRecipes = new ArrayList<>();

                for (RecipeDTO r : recipes) {
                    String iconPath = null;

                    if (r.iconPath != null) {
                        Path p = Paths.get(r.iconPath);
                        imagesToAdd.add(p);
                        iconPath = "images/" + p.getFileName();
                    }

                    exportedRecipes.add(DTOFactory.recipe(
                            r.ingredients,
                            r.results,
                            r.name,
                            iconPath,
                            r.description,
                            r.id
                    ));
                }

                collMap.put("recipes", exportedRecipes);

                collectionsData.add(collMap);
            }

            // ===== JSON =====
            ZipEntry jsonEntry = new ZipEntry("data.json");
            zos.putNextEntry(jsonEntry);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            zos.write(gson.toJson(collectionsData).getBytes());

            zos.closeEntry();

            // ===== IMAGES =====
            for (Path imgPath : imagesToAdd) {
                ZipEntry imgEntry =
                        new ZipEntry("images/" + imgPath.getFileName());

                zos.putNextEntry(imgEntry);
                Files.copy(imgPath, zos);
                zos.closeEntry();
            }
 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

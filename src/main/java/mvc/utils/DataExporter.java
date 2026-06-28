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
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.RecipeDTO;
import javafx.stage.FileChooser; 
import service.CollectionService;
import service.ItemService;
import service.RecipeService;
import service.ServiceConsumer;
import service.ServiceType;
import service.SessionService;

public class DataExporter extends ServiceConsumer {

    public void exportUserData() {
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        CollectionDTO currentCollection = sessionService.getCurrentCollectionDTO();

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

            for (CollectionDTO c : collectionService.getAllDTO(sessionService.getCurrentAccount().getId().value())) {

                Map<String, Object> collMap = new LinkedHashMap<>();

                // ===== COLLECTION =====
                String collIcon = null;
                if (c.imagePath != null) {
                    Path p = Paths.get(c.imagePath);
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
                List<ItemDTO> items = itemService.getAllDTO(currentCollection.id);

                List<ItemDTO> exportedItems = new ArrayList<>();

                for (ItemDTO i : items) {
                    String iconPath = null;

                    if (i.imagePath != null) {
                        Path p = Paths.get(i.imagePath);
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
                List<RecipeDTO> recipes = recipeService.getAllDTO(currentCollection.id);

                List<RecipeDTO> exportedRecipes = new ArrayList<>();

                for (RecipeDTO r : recipes) {
                    String iconPath = null;

                    if (r.imagePath != null) {
                        Path p = Paths.get(r.imagePath);
                        imagesToAdd.add(p);
                        iconPath = "images/" + p.getFileName();
                    }

                    for (ItemIdStackDTO ing : r.ingredients) ing.name = itemService.getDTOById(ing.id).name;
                    for (ItemIdStackDTO res : r.results) res.name = itemService.getDTOById(res.id).name;

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

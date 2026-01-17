package mvc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creational.DTOFactory;
import dataTransportLayer.*;
import javafx.stage.FileChooser;
import mvc.context.RuntimeContext;
import mvc.model.entries.Collection;
import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
import mvc.model.entries.repository.EntryIdGenerator;
import dataAccessLayer.DAO.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class DataImporter {

    private final RuntimeContext context;

    public DataImporter(RuntimeContext context) {
        this.context = context;
    }

    public void importUserData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar datos del usuario");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP files", "*.zip"));
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        Map<String, Path> imagesMap = new HashMap<>();
        List<Map<String, Object>> collectionsData = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("data.json")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    String json = baos.toString();
                    Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                    collectionsData = new Gson().fromJson(json, listType);
                } else if (entry.getName().startsWith("images/")) {
                    Path tempImg = Files.createTempFile("import_img_", "_" + Paths.get(entry.getName()).getFileName());
                    Files.copy(zis, tempImg, StandardCopyOption.REPLACE_EXISTING);
                    imagesMap.put(Paths.get(entry.getName()).getFileName().toString(), tempImg);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (collectionsData == null) return;

        EntryIdGenerator idGen = EntryIdGenerator.getInstance();

        for (Map<String, Object> collMap : collectionsData) {
            Map<String, Object> collData = (Map<String, Object>) collMap.get("collection");
            String collectionName = (String) collData.get("name");
            String iconName = (String) collData.get("iconPath");
            Path iconPath = iconName != null
                    ? imagesMap.get(Paths.get(iconName).getFileName().toString())
                    : null;

            String description = (String) collData.get("description");

            if (context.getCollections().stream().anyMatch(c -> c.name.equals(collectionName))) continue;

            Collection newCollection = context.getEntriesFactory().createCollection(
                DTOFactory.collection(
                    null,
                    null,
                    collectionName,
                    iconPath != null ? iconPath.toString() : null,
                    description,
                    idGen.generateId()
                )
            );
            CollectionDAO collDAO = (CollectionDAO) context.getDAO(DAOType.COLLECTION);
            collDAO.create(newCollection, new int[]{context.getAccount().getId().value()});

            // Map para traducir IDs viejos -> Items nuevos
            Map<Integer, Item> oldIdToNewItem = new HashMap<>();

            List<Map<String, Object>> items = (List<Map<String, Object>>) collMap.get("items");
            if (items != null) {
                ItemDAO itemDAO = (ItemDAO) context.getDAO(DAOType.ITEM);
                for (Map<String, Object> itemData : items) {
                    String imgName = (String) itemData.get("iconPath");
                    Path imgPath = imgName != null ? imagesMap.get(Paths.get(imgName).getFileName().toString()) : null;

                    int oldId = ((Double) itemData.get("id")).intValue();
                    int newId = idGen.generateId();

                    Item newItem = context.getEntriesFactory().createItem(DTOFactory.item(
                            (String) itemData.get("name"),
                            imgPath != null ? imgPath.toString() : null,
                            (String) itemData.get("description"),
                            newId
                    ));

                    itemDAO.create(newItem, new int[]{newCollection.getId().value()});
                    oldIdToNewItem.put(oldId, newItem);
                }
            }

            List<Map<String, Object>> recipes = (List<Map<String, Object>>) collMap.get("recipes");
            if (recipes != null) {
                RecipeDAO recipeDAO = (RecipeDAO) context.getDAO(DAOType.RECIPE);
                for (Map<String, Object> recipeData : recipes) {
                    String imgName = (String) recipeData.get("iconPath");
                    Path imgPath = imgName != null ? imagesMap.get(Paths.get(imgName).getFileName().toString()) : null;

                    Recipe newRecipe = context.getEntriesFactory().createRecipe(DTOFactory.recipe(
                            new ArrayList<>(), 
                            new ArrayList<>(), 
                            (String) recipeData.get("name"), 
                            imgPath != null ? imgPath.toString() : null, 
                            (String) recipeData.get("description"), 
                            idGen.generateId()
                    ));

                    List<Map<String, Object>> ingredientsRaw = (List<Map<String, Object>>) recipeData.get("ingredients");
                    if (ingredientsRaw != null) {
                        for (Map<String, Object> ing : ingredientsRaw) {
                            int oldId = ((Double) ing.get("id")).intValue();
                            int amount = ((Double) ing.get("amount")).intValue();
                            Item realItem = oldIdToNewItem.get(oldId);
                            if (realItem != null) newRecipe.addIngredient(realItem, amount);
                        }
                    }

                    List<Map<String, Object>> resultsRaw = (List<Map<String, Object>>) recipeData.get("results");
                    if (resultsRaw != null) {
                        for (Map<String, Object> res : resultsRaw) {
                            int oldId = ((Double) res.get("id")).intValue();
                            int amount = ((Double) res.get("amount")).intValue();
                            Item realItem = oldIdToNewItem.get(oldId);
                            if (realItem != null) newRecipe.addResult(realItem, amount);
                        }
                    }

                    recipeDAO.create(newRecipe, new int[]{newCollection.getId().value()});
                }
            }
        }
    }
}

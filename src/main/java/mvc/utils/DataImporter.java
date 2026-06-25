package mvc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creational.DTOFactory;
import dataTransportLayer.*;
import javafx.stage.FileChooser;
import logger.Logger;
import mvc.context.RuntimeContext;
import mvc.model.entries.Collection;
import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
import mvc.model.entries.repository.EntryIdGenerator;
import service.CollectionService;
import service.ItemService;
import service.RecipeService;
import service.ServiceConsumer;
import service.ServiceType;
import service.SessionService;
import dataAccessLayer.DAO.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class DataImporter extends ServiceConsumer { 

    public List<RecipeDTO> importUserData() {
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        ItemService itemService = this.getService(ServiceType.ITEM);
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        
        List<RecipeDTO> wrongRecipes = new ArrayList<>();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar datos del usuario");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP files", "*.zip"));
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return wrongRecipes;

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
            return wrongRecipes;
        }

        if (collectionsData == null) return wrongRecipes;

        EntryIdGenerator idGen = EntryIdGenerator.getInstance();

        for (Map<String, Object> collMap : collectionsData) {
            Map<String, Object> collData = (Map<String, Object>) collMap.get("collection");
            String collectionName = (String) collData.get("name");
            CollectionDTO oldCollectionDTO = collectionService.getDTOByName(collectionName); 
            
            String collectionDescription = (String) collData.get("description");
            String collectionImgName = (String) collData.get("iconPath");
            Path colectionImgPath = collectionImgName != null
                    ? imagesMap.get(Paths.get(collectionImgName).getFileName().toString())
                    : null; 

            String resolvedCImg  = colectionImgPath != null ? colectionImgPath.toString()
                                : oldCollectionDTO != null ? oldCollectionDTO.imagePath
                                : null;
            String resolvedCDesc = collectionDescription != null ? collectionDescription
                                : oldCollectionDTO      != null ? oldCollectionDTO.description
                                : null;
            int resolvedCId   = oldCollectionDTO != null ? oldCollectionDTO.id : idGen.generateId();

            CollectionDTO newCollection = DTOFactory.collection(
                    null,
                    null,
                    collectionName,
                    resolvedCImg,
                    resolvedCDesc,
                    resolvedCId
                ); 
            int[] extraData = new int[]{sessionService.getCurrentAccount().getId().value()};
            collectionService.saveFromImport(newCollection, extraData); 

            // Map para traducir IDs viejos -> Items nuevos
            Map<Integer, Item> oldIdToNewItem = new HashMap<>();

            List<Map<String, Object>> items = (List<Map<String, Object>>) collMap.get("items");
            for (Map<String, Object> itemData : items) {
                String itemName = (String) itemData.get("name");
                ItemDTO oldItemDTO = itemService.getDTOByName(itemName);

                String itemDescription = (String) itemData.get("description");
                String itemImgName    = (String) itemData.get("iconPath");
                Path itemImgPath = itemImgName != null 
                    ? imagesMap.get(Paths.get(itemImgName).getFileName().toString()) 
                    : null;

                // Extraer valores dependientes de oldItemDTO
                String resolvedImg  = itemImgPath  != null ? itemImgPath.toString()
                                    : oldItemDTO   != null ? oldItemDTO.imagePath 
                                    : null;
                String resolvedDesc = itemDescription != null ? itemDescription
                                    : oldItemDTO      != null ? oldItemDTO.description 
                                    : null;
                int    resolvedId   = oldItemDTO != null ? oldItemDTO.id : idGen.generateId();

                ItemDTO newItemDTO = DTOFactory.item(itemName, resolvedImg, resolvedDesc, resolvedId);
                itemService.saveFromImport(newItemDTO, new int[]{newCollection.id});
            }

            List<Map<String, Object>> recipes = (List<Map<String, Object>>) collMap.get("recipes");
            if (recipes != null) { 
                for (Map<String, Object> recipeData : recipes) {
                    String recipeName = (String) recipeData.get("name");
                    RecipeDTO oldRecipeDTO = recipeService.getDTOByName(recipeName);

                    String recipeDescription = (String) recipeData.get("description");
                    String recipeImgName     = (String) recipeData.get("iconPath");
                    Path recipeImgPath = recipeImgName != null
                        ? imagesMap.get(Paths.get(recipeImgName).getFileName().toString())
                        : null;

                    String resolvedImg  = recipeImgPath    != null ? recipeImgPath.toString()
                                        : oldRecipeDTO     != null ? oldRecipeDTO.imagePath
                                        : null;
                    String resolvedDesc = recipeDescription != null ? recipeDescription
                                        : oldRecipeDTO      != null ? oldRecipeDTO.description
                                        : null;
                    int    resolvedId   = oldRecipeDTO != null ? oldRecipeDTO.id : idGen.generateId();

                    RecipeDTO newRecipeDTO = DTOFactory.recipe(
                            new ArrayList<>(),
                            new ArrayList<>(),
                            recipeName,
                            resolvedImg,
                            resolvedDesc,
                            resolvedId
                    );

                    List<Map<String, Object>> ingredientsRaw = (List<Map<String, Object>>) recipeData.get("ingredients");
                    if (ingredientsRaw != null && ingredientsRaw.size() > 0) {
                        for (Map<String, Object> ing : ingredientsRaw) {
                            String name = ((String) ing.get("name"));
                            int amount = ((Double) ing.get("amount")).intValue();
                            
                            ItemDTO item = itemService.getDTOByName(name); 
                            if (item != null) newRecipeDTO.ingredients.add(new ItemIdStackDTO(item.id, amount));
                            else { 
                                Logger.getInstance().error("DataImporter", "No se encontró el item '" + name + "' (input) para la receta '" + recipeName + "'. Omitiendo receta.");
                                if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                            }
                        }
                    } else {
                        Logger.getInstance().error("DataImporter", "La receta '" + recipeName + "' no tiene ingredientes. Omitiendo receta.");
                        if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                    }

                    List<Map<String, Object>> resultsRaw = (List<Map<String, Object>>) recipeData.get("results");
                    if (resultsRaw != null && resultsRaw.size() > 0) {
                        for (Map<String, Object> res : resultsRaw) {
                            String name = ((String) res.get("name"));
                            int amount = ((Double) res.get("amount")).intValue();
                            
                            ItemDTO item = itemService.getDTOByName(name); 
                            if (item != null) newRecipeDTO.results.add(new ItemIdStackDTO(item.id, amount));
                            else { 
                                Logger.getInstance().error("DataImporter", "No se encontró el item '" + name + "' (output) para la receta '" + recipeName + "'. Omitiendo receta.");
                                if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                            }
                        }
                    } else {
                        Logger.getInstance().error("DataImporter", "La receta '" + recipeName + "' no tiene resultados. Omitiendo receta.");
                        if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                    }

                    if (!wrongRecipes.contains(newRecipeDTO)) {
                        recipeService.saveFromImport(newRecipeDTO, new int[]{newCollection.id});
                    }
                }
            }
        }
        return wrongRecipes;
    }
}

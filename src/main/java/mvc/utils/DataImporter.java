package mvc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creational.DTOFactory;
import dataTransportLayer.*;
import domain.accounts.Account;
import javafx.stage.FileChooser;
import logger.Logger;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.model.entries.component.ItemComponentValue;
import mvc.model.entries.repository.EntryIdGenerator;
import service.CollectionService;
import service.ComponentService;
import service.ItemService;
import service.RecipeService;
import service.ServiceConsumer;
import service.ServiceType;
import service.SessionService;
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
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        List<RecipeDTO> wrongRecipes = new ArrayList<>();

        Account currentAccount = sessionService.getCurrentAccount();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar datos del usuario");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP files", "*.zip"));
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return wrongRecipes;

        Map<String, Path> imagesMap = new HashMap<>();
        Map<String, Object> root = null;

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
                    Type rootType = new TypeToken<Map<String, Object>>() {}.getType();
                    root = new Gson().fromJson(json, rootType);
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

        if (root == null) return wrongRecipes;

        EntryIdGenerator idGen = EntryIdGenerator.getInstance();
        int accountId = sessionService.getCurrentAccount().getId().value();

        // ===== COMPONENTS (a nivel de cuenta) =====
        Map<Integer, Integer> oldDefIdToNewDefId = new HashMap<>();
        Map<Integer, List<ComponentField>> newDefIdToFields = new HashMap<>();

        List<Map<String, Object>> componentsRaw = (List<Map<String, Object>>) root.get("components");
        if (componentsRaw != null) {
            for (Map<String, Object> compData : componentsRaw) {
                String compName = (String) compData.get("name");
                ComponentDefinitionDTO oldCompDTO = componentService.getDTOByName(compName, currentAccount.getId().value());

                String compDescription = (String) compData.get("description");
                String compImgName = (String) compData.get("imagePath");
                Path compImgPath = compImgName != null
                        ? imagesMap.get(Paths.get(compImgName).getFileName().toString())
                        : null;

                String resolvedImg  = compImgPath != null ? compImgPath.toString()
                                    : oldCompDTO   != null ? oldCompDTO.imagePath
                                    : null;
                String resolvedDesc = compDescription != null ? compDescription
                                    : oldCompDTO      != null ? oldCompDTO.description
                                    : null;

                Object oldIdObj = compData.get("id");
                int oldDefId = oldIdObj != null ? ((Double) oldIdObj).intValue() : -1;
                int resolvedId = oldCompDTO != null ? oldCompDTO.id : idGen.generateId();

                List<Map<String, Object>> fieldsRaw = (List<Map<String, Object>>) compData.get("fields");
                List<ComponentField> fields = new ArrayList<>();
                if (fieldsRaw != null) {
                    for (Map<String, Object> fieldData : fieldsRaw) {
                        String fieldName = (String) fieldData.get("fieldName");
                        FieldType fieldType = FieldType.valueOf((String) fieldData.get("fieldType"));
                        List<String> enumValues = (List<String>) fieldData.get("enumValues");
                        fields.add(new ComponentField(fieldName, fieldType, enumValues != null ? enumValues : new ArrayList<>()));
                    }
                }

                ComponentDefinitionDTO newCompDTO = DTOFactory.component(resolvedId, compName, resolvedImg, resolvedDesc, fields);
                componentService.saveFromImport(newCompDTO, new int[]{accountId});

                if (oldDefId != -1) oldDefIdToNewDefId.put(oldDefId, resolvedId);
                newDefIdToFields.put(resolvedId, fields);
            }
        }

        List<Map<String, Object>> collectionsData = (List<Map<String, Object>>) root.get("collections");
        if (collectionsData == null) return wrongRecipes;

        // PASADA 1: collections + items (todo item disponible antes de tocar recetas)
        Map<String, Integer> collectionNameToId = new HashMap<>();
        Map<Integer, ItemDTO> oldIdToNewItem = new HashMap<>();

        for (Map<String, Object> collMap : collectionsData) {
            Map<String, Object> collData = (Map<String, Object>) collMap.get("collection");
            String collectionName = (String) collData.get("name");
            CollectionDTO oldCollectionDTO = collectionService.getDTOByName(collectionName, currentAccount.getId().value());

            String collectionDescription = (String) collData.get("description");
            String collectionImgName = (String) collData.get("imagePath");
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
            int[] extraData = new int[]{accountId};
            collectionService.saveFromImport(newCollection, extraData);

            collectionNameToId.put(collectionName, newCollection.id);

            List<Map<String, Object>> items = (List<Map<String, Object>>) collMap.get("items");
            for (Map<String, Object> itemData : items) {
                String itemName = (String) itemData.get("name");
                ItemDTO oldItemDTO = itemService.getDTOByName(itemName, newCollection.id);

                String itemDescription = (String) itemData.get("description");
                String itemImgName    = (String) itemData.get("imagePath");
                Path itemImgPath = itemImgName != null
                    ? imagesMap.get(Paths.get(itemImgName).getFileName().toString())
                    : null;

                String resolvedImg  = itemImgPath  != null ? itemImgPath.toString()
                                    : oldItemDTO   != null ? oldItemDTO.imagePath
                                    : null;
                String resolvedDesc = itemDescription != null ? itemDescription
                                    : oldItemDTO      != null ? oldItemDTO.description
                                    : null;
                int    resolvedId   = oldItemDTO != null ? oldItemDTO.id : idGen.generateId();

                // ===== COMPONENTES DEL ITEM =====
                List<ItemComponentValue> components = new ArrayList<>();
                List<Map<String, Object>> componentsRawForItem = (List<Map<String, Object>>) itemData.get("components");
                if (componentsRawForItem != null) {
                    for (Map<String, Object> compValueData : componentsRawForItem) {
                        Object oldDefIdObj = compValueData.get("componentDefId");
                        int oldDefId = oldDefIdObj != null ? ((Double) oldDefIdObj).intValue() : -1;
                        Integer newDefId = oldDefIdToNewDefId.get(oldDefId);
                        if (newDefId == null) {
                            Logger.getInstance().error("DataImporter", "No se encontró el componente id '" + oldDefId + "' para el item '" + itemName + "'. Omitiendo componente.");
                            continue;
                        }
                        Map<String, String> fieldValues = (Map<String, String>) compValueData.get("fieldValues");
                        components.add(new ItemComponentValue(newDefId, fieldValues != null ? new HashMap<>(fieldValues) : new HashMap<>()));
                    }
                }

                ItemDTO newItemDTO = DTOFactory.item(itemName, resolvedImg, resolvedDesc, resolvedId, components);
                itemService.saveFromImport(newItemDTO, new int[]{newCollection.id});

                Object oldItemId = itemData.get("id");
                if (oldItemId != null) oldIdToNewItem.put(((Double) oldItemId).intValue(), newItemDTO);
            }
        }

        // PASADA 2: recipes (todos items ya existen, sin importar orden de colecciones)
        for (Map<String, Object> collMap : collectionsData) {
            Map<String, Object> collData = (Map<String, Object>) collMap.get("collection");
            String collectionName = (String) collData.get("name");
            int collectionId = collectionNameToId.get(collectionName);

            List<Map<String, Object>> recipes = (List<Map<String, Object>>) collMap.get("recipes");
            if (recipes == null) continue;

            for (Map<String, Object> recipeData : recipes) {
                String recipeName = (String) recipeData.get("name");
                RecipeDTO oldRecipeDTO = recipeService.getDTOByName(recipeName, collectionId);

                String recipeDescription = (String) recipeData.get("description");
                String recipeImgName     = (String) recipeData.get("imagePath");
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
                        int oldId = ((Double) ing.get("id")).intValue();
                        int amount = ((Double) ing.get("amount")).intValue();

                        ItemDTO item = oldIdToNewItem.get(oldId);
                        if (item != null) newRecipeDTO.ingredients.add(new ItemIdStackDTO(item.id, amount));
                        else {
                            Logger.getInstance().error("DataImporter", "No se encontró el item id '" + oldId + "' (input) para la receta '" + recipeName + "'. Omitiendo receta.");
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
                        int oldId = ((Double) res.get("id")).intValue();
                        int amount = ((Double) res.get("amount")).intValue();

                        ItemDTO item = oldIdToNewItem.get(oldId);
                        if (item != null) newRecipeDTO.results.add(new ItemIdStackDTO(item.id, amount));
                        else {
                            Logger.getInstance().error("DataImporter", "No se encontró el item id '" + oldId + "' (output) para la receta '" + recipeName + "'. Omitiendo receta.");
                            if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                        }
                    }
                } else {
                    Logger.getInstance().error("DataImporter", "La receta '" + recipeName + "' no tiene resultados. Omitiendo receta.");
                    if (!wrongRecipes.contains(newRecipeDTO)) wrongRecipes.add(newRecipeDTO);
                }

                if (!wrongRecipes.contains(newRecipeDTO)) {
                    recipeService.saveFromImport(newRecipeDTO, new int[]{collectionId});
                }
            }
        }

        return wrongRecipes;
    }

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.COLLECTION);
        out.add(ServiceType.ITEM);
        out.add(ServiceType.RECIPE);
        out.add(ServiceType.COMPONENT);
        out.add(ServiceType.SESSION);
        return out;
    }
}
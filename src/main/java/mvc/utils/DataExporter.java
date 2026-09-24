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
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.RecipeDTO;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import mvc.model.entries.component.ItemComponentValue;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;
import utilities.ModelFileStore;
import service.CollectionService;
import service.ComponentService;
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
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);

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
            Map<String, Path> modelsToAdd = new LinkedHashMap<>();

            // ===== COMPONENTS (a nivel de cuenta) =====
            List<ComponentDefinitionDTO> components = componentService.getAllDTO(sessionService.getCurrentAccount().getId().value());
            List<ComponentDefinitionDTO> exportedComponents = new ArrayList<>();

            for (ComponentDefinitionDTO comp : components) {
                String compIcon = null;
                if (comp.imagePath != null) {
                    Path p = Paths.get(comp.imagePath);
                    imagesToAdd.add(p);
                    compIcon = "images/" + p.getFileName();
                }
                exportedComponents.add(DTOFactory.component(
                        comp.id,
                        comp.name,
                        compIcon,
                        comp.description,
                        comp.fields
                ));
            }

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
                List<ItemDTO> items = itemService.getAllDTO(c.id);

                List<ItemDTO> exportedItems = new ArrayList<>();

                for (ItemDTO i : items) {
                    String iconPath = null;

                    if (i.imagePath != null) {
                        Path p = Paths.get(i.imagePath);
                        imagesToAdd.add(p);
                        iconPath = "images/" + p.getFileName();
                    }

                    // Aqui los modelos viajan con su nombre almacenado y no con el nombre
                    // legible de la exportacion de coleccion. Esto es una copia entre
                    // equipos, no material para el juego: lo que importa es poder devolver
                    // cada fichero a su etapa, y el nombre almacenado es justo lo que las
                    // etapas referencian.
                    for (ItemModelStage stage : i.modelStages) {
                        for (ModelFile model : stage.getFiles()) {
                            Path modelPath = ModelFileStore.resolve(model.getStoredName());
                            if (modelPath != null && Files.isRegularFile(modelPath)) {
                                modelsToAdd.put("models/" + model.getStoredName(), modelPath);
                            }
                        }
                    }

                    exportedItems.add(DTOFactory.item(
                            i.name,
                            iconPath,
                            i.description,
                            i.id,
                            i.components,
                            i.modelDrivenBy,
                            i.modelStages
                    ));
                }

                collMap.put("items", exportedItems);

                // ===== RECIPES =====
                List<RecipeDTO> recipes = recipeService.getAllDTO(c.id);

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
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("components", exportedComponents);
            root.put("collections", collectionsData);

            ZipEntry jsonEntry = new ZipEntry("data.json");
            zos.putNextEntry(jsonEntry);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            zos.write(gson.toJson(root).getBytes());

            zos.closeEntry();

            // ===== IMAGES =====
            for (Path imgPath : imagesToAdd) {
                ZipEntry imgEntry =
                        new ZipEntry("images/" + imgPath.getFileName());

                zos.putNextEntry(imgEntry);
                Files.copy(imgPath, zos);
                zos.closeEntry();
            }

            // ===== MODELS =====
            for (Map.Entry<String, Path> model : modelsToAdd.entrySet()) {
                zos.putNextEntry(new ZipEntry(model.getKey()));
                Files.copy(model.getValue(), zos);
                zos.closeEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportCollection() {
        ItemService itemService = this.getService(ServiceType.ITEM);
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ComponentService componentService = this.getService(ServiceType.COMPONENT);

        CollectionDTO c = sessionService.getCurrentCollectionDTO();

        List<ItemDTO> collectionItems = itemService.getAllDTO(c.id);
        List<String> problems = ModelExporter.validate(
                collectionItems,
                componentService.getAllDTO(sessionService.getCurrentAccount().getId().value())
        );

        if (!problems.isEmpty()) {
            reportProblems(problems);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar colección");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP files", "*.zip"));
        fileChooser.setInitialFileName(c.name + ".zip");
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {

            Set<Path> imagesToAdd = new HashSet<>();
            Map<String, Path> modelsToAdd = new LinkedHashMap<>();
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("collection", c.name);

            // ===== ITEMS =====
            List<Map<String, Object>> exportedItems = new ArrayList<>();
            for (ItemDTO i : collectionItems) {
                Map<String, Object> itemMap = new LinkedHashMap<>();
                itemMap.put("id", i.id);
                itemMap.put("name", i.name);
                itemMap.put("description", i.description);

                String iconPath = null;
                if (i.imagePath != null) {
                    Path p = Paths.get(i.imagePath);
                    imagesToAdd.add(p);
                    iconPath = "images/" + p.getFileName();
                }
                itemMap.put("imagePath", iconPath);

                List<Map<String, Object>> comps = new ArrayList<>();
                for (ItemComponentValue v : i.components) {
                    Map<String, Object> compMap = new LinkedHashMap<>();
                    compMap.put("type", componentService.getDTOById(v.getComponentDefId()).name);
                    compMap.put("values", parseFieldValues(v.getFieldValues()));
                    comps.add(compMap);
                }

                // Los modelos viajan como un componente mas, no como una seccion aparte del
                // JSON: para el juego "que modelo muestro" es una propiedad del item igual
                // que su peso, y darle forma propia obligaria a leer el fichero de dos
                // maneras distintas.
                ModelExporter.ItemModels models = ModelExporter.of(i);
                if (models.component != null) {
                    comps.add(models.component);
                    modelsToAdd.putAll(models.entries);
                }

                itemMap.put("components", comps);

                exportedItems.add(itemMap);
            }
            root.put("items", exportedItems);

            // ===== RECIPES =====
            List<Map<String, Object>> exportedRecipes = new ArrayList<>();
            for (RecipeDTO r : recipeService.getAllDTO(c.id)) {
                Map<String, Object> recMap = new LinkedHashMap<>();
                recMap.put("name", r.name);
                recMap.put("description", r.description);

                String iconPath = null;
                if (r.imagePath != null) {
                    Path p = Paths.get(r.imagePath);
                    imagesToAdd.add(p);
                    iconPath = "images/" + p.getFileName();
                }
                recMap.put("imagePath", iconPath);

                List<Map<String, Object>> ingredients = new ArrayList<>();
                for (ItemIdStackDTO ing : r.ingredients) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("item", itemService.getDTOById(ing.id).name);
                    m.put("amount", ing.amount);
                    ingredients.add(m);
                }
                recMap.put("ingredients", ingredients);

                List<Map<String, Object>> results = new ArrayList<>();
                for (ItemIdStackDTO res : r.results) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("item", itemService.getDTOById(res.id).name);
                    m.put("amount", res.amount);
                    results.add(m);
                }
                recMap.put("results", results);

                exportedRecipes.add(recMap);
            }
            root.put("recipes", exportedRecipes);

            // ===== JSON =====
            ZipEntry jsonEntry = new ZipEntry("data.json");
            zos.putNextEntry(jsonEntry);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            zos.write(gson.toJson(root).getBytes());
            zos.closeEntry();

            // ===== IMAGES =====
            for (Path imgPath : imagesToAdd) {
                ZipEntry imgEntry = new ZipEntry("images/" + imgPath.getFileName());
                zos.putNextEntry(imgEntry);
                Files.copy(imgPath, zos);
                zos.closeEntry();
            }

            // ===== MODELS =====
            for (Map.Entry<String, Path> model : modelsToAdd.entrySet()) {
                zos.putNextEntry(new ZipEntry(model.getKey()));
                Files.copy(model.getValue(), zos);
                zos.closeEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Avisa de por que no se ha exportado.
     *
     * <p>Se listan todos los problemas de una vez en lugar de parar en el primero: quien
     * exporta quiere saber cuanto trabajo tiene por delante, no descubrirlo de uno en
     * uno.</p>
     *
     * @param problems descripciones de lo que impide exportar
     */
    private void reportProblems(List<String> problems) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No se puede exportar");
        alert.setHeaderText("Hay problemas con los modelos 3D de esta coleccion.");
        alert.setContentText(String.join("\n", problems));
        alert.getDialogPane().setPrefWidth(520);
        alert.showAndWait();
    }

    private Map<String, Object> parseFieldValues(Map<String, String> raw) {
        Map<String, Object> parsed = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            parsed.put(entry.getKey(), parseValue(entry.getValue()));
        }
        return parsed;
    }

    private Object parseValue(String value) {
        if (value == null) return null;

        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) {}

        try { return Float.parseFloat(value); }
        catch (NumberFormatException ignored) {}

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
            return Boolean.parseBoolean(value);

        return value;
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
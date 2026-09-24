package mvc.utils;

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;
import mvc.model.entries.model3d.ModelMagnitude;
import utilities.ModelFileStore;

/**
 * Traduce los modelos de un item a lo que viaja en una exportacion de coleccion.
 *
 * <p>Dentro de la aplicacion un modelo es una fila y un fichero con nombre opaco; fuera es
 * una entrada de un zip y un componente mas del item. Esa traduccion se hace aqui y no en
 * {@link DataExporter} porque tiene reglas propias —como se saneia un nombre, como se
 * serializa una etapa, que hace que una exportacion no sea valida— que no tienen que ver con
 * armar un zip.</p>
 *
 * <p>El nombre con el que un modelo viaja se construye en este momento y no antes: solo
 * aqui se conocen a la vez el item, su identificador y la posicion del fichero, que es lo
 * que lo hace legible y unico. Dentro de la aplicacion ese nombre no serviria de nada,
 * porque un item puede cambiar de nombre y el fichero no tendria por que enterarse.</p>
 */
public final class ModelExporter {

    /** Carpeta plana dentro del zip donde caen todos los modelos. */
    public static final String ZIP_FOLDER = "models/";

    /**
     * Nombre del componente que el juego lee para saber que modelo mostrar.
     *
     * <p>Sin el sufijo "Component" porque asi se llaman los demas en el JSON —BaseItem,
     * Material, Wearable— y ese nombre es la clave con la que el juego los busca en su
     * registro. Que este fuera el unico con sufijo no rompe nada, pero convierte una
     * convencion en una excepcion que hay que recordar.</p>
     */
    public static final String COMPONENT_NAME = "Model";

    private ModelExporter() {
        // No se instancia. Esto no es un objeto, es una herramienta.
    }

    /**
     * Lo que un item aporta a la exportacion: los ficheros que hay que meter en el zip y el
     * componente que lo describe.
     */
    public static class ItemModels {
        /** Nombre de entrada dentro del zip, apuntando al fichero real en disco. */
        public final Map<String, Path> entries = new LinkedHashMap<>();

        /** Componente a anadir a la lista del item, o null si el item no tiene modelos. */
        public Map<String, Object> component;
    }

    /**
     * Prepara lo que aporta un item.
     *
     * <p>Un item sin modelos devuelve un resultado vacio y sin componente. No se emite un
     * componente sin contenido porque en el juego no tenerlo y tenerlo vacio significarian
     * lo mismo, y de las dos formas de decirlo solo una evita comprobar el interior.</p>
     *
     * @param item item ya leido de base de datos
     * @return ficheros y componente que le corresponden
     */
    public static ItemModels of(ItemDTO item) {
        ItemModels out = new ItemModels();
        if (item.modelStages == null || item.modelStages.isEmpty()) return out;

        List<String> stageChunks = new ArrayList<>();
        int index = 0;

        for (ItemModelStage stage : item.modelStages) {
            if (stage.isEmpty()) continue;

            List<String> names = new ArrayList<>();

            for (ModelFile file : stage.getFiles()) {
                if (file.isPending()) continue;

                String entryName = ZIP_FOLDER + entryNameFor(item, index++);
                out.entries.put(entryName, ModelFileStore.resolve(file.getStoredName()));
                names.add(entryName);
            }

            if (!names.isEmpty()) {
                stageChunks.add(format(stage.getThreshold()) + "=" + String.join(",", names));
            }
        }

        if (stageChunks.isEmpty()) return out;

        Map<String, Object> values = new LinkedHashMap<>();
        if (item.modelDrivenBy != null && !item.modelDrivenBy.isBlank()) {
            values.put("drivenBy", item.modelDrivenBy);
        }
        values.put("stages", String.join(";", stageChunks));

        Map<String, Object> component = new LinkedHashMap<>();
        component.put("type", COMPONENT_NAME);
        component.put("values", values);

        out.component = component;
        return out;
    }

    /**
     * Revisa los modelos de una coleccion entera y devuelve lo que impide exportarla.
     *
     * <p>Se comprueba antes de empezar a escribir el zip. Una exportacion a medias es peor
     * que ninguna: el autor se lleva un fichero que parece bueno y el fallo aparece dentro
     * del juego, lejos de donde se puede arreglar.</p>
     *
     * <p>Lo que se comprueba es lo que la aplicacion no puede garantizar por si sola. Que no
     * haya umbrales repetidos lo impone la base de datos, pero que el fichero siga en disco
     * o que la magnitud siga existiendo depende de cosas que pasaron fuera: alguien borro el
     * .glb, o quito ese campo del componente meses despues de elegirlo.</p>
     *
     * @param items items de la coleccion
     * @param definitions definiciones de componente de la cuenta
     * @return lista de problemas, vacia si todo esta en orden
     */
    public static List<String> validate(List<ItemDTO> items, List<ComponentDefinitionDTO> definitions) {
        List<String> problems = new ArrayList<>();
        List<String> magnitudes = numericMagnitudes(definitions);

        for (ItemDTO item : items) {
            if (item.modelStages == null || item.modelStages.isEmpty()) continue;

            List<Float> seen = new ArrayList<>();
            boolean hasAnyFile = false;

            for (ItemModelStage stage : item.modelStages) {
                if (seen.contains(stage.getThreshold())) {
                    problems.add(item.name + ": umbral " + format(stage.getThreshold()) + " repetido.");
                }
                seen.add(stage.getThreshold());

                if (stage.isEmpty()) {
                    problems.add(item.name + ": la etapa " + format(stage.getThreshold()) + " no tiene ningun modelo.");
                    continue;
                }

                for (ModelFile file : stage.getFiles()) {
                    if (file.isPending() || !ModelFileStore.exists(file.getStoredName())) {
                        problems.add(item.name + ": falta el fichero '" + file + "'.");
                    } else {
                        hasAnyFile = true;
                    }
                }
            }

            boolean needsMagnitude = countUsableStages(item) > 1;

            if (needsMagnitude && (item.modelDrivenBy == null || item.modelDrivenBy.isBlank())) {
                problems.add(item.name + ": tiene varias etapas pero no indica de que magnitud dependen.");
            }

            if (item.modelDrivenBy != null && !item.modelDrivenBy.isBlank()
                    && !magnitudes.contains(item.modelDrivenBy)) {
                problems.add(item.name + ": la magnitud '" + item.modelDrivenBy
                        + "' ya no existe o ha dejado de ser numerica.");
            }

            if (!hasAnyFile && !item.modelStages.isEmpty()) {
                problems.add(item.name + ": tiene etapas pero ningun modelo utilizable.");
            }
        }

        return problems;
    }

    /**
     * Etapas de un item que aportan algo a la exportacion.
     */
    private static int countUsableStages(ItemDTO item) {
        int count = 0;
        for (ItemModelStage stage : item.modelStages) {
            if (!stage.isEmpty()) count++;
        }
        return count;
    }

    /**
     * Magnitudes cualificadas que un item puede declarar, tal como las ofrece el editor.
     */
    private static List<String> numericMagnitudes(List<ComponentDefinitionDTO> definitions) {
        List<String> out = new ArrayList<>();
        if (definitions == null) return out;

        for (ComponentDefinitionDTO def : definitions) {
            if (def.fields == null) continue;

            for (ComponentField field : def.fields) {
                if (field.getFieldType() == FieldType.FLOAT || field.getFieldType() == FieldType.INT) {
                    out.add(ModelMagnitude.qualify(def.name, field.getFieldName()));
                }
            }
        }
        return out;
    }

    /**
     * Nombre con el que un fichero de un item viaja dentro del zip.
     *
     * @param item item al que pertenece
     * @param index posicion del fichero dentro del item, empezando en cero
     */
    private static String entryNameFor(ItemDTO item, int index) {
        return item.id + "_" + sanitize(item.name) + "_" + String.format("%02d", index) + ModelFileStore.EXTENSION;
    }

    /**
     * Reduce un nombre a minusculas, digitos y guion bajo.
     *
     * <p>Lo que sale de aqui acaba siendo el nombre de un fichero que abrira otro programa
     * en otro sistema. Las tildes y los espacios sobreviven mal a ese viaje, y el nombre no
     * necesita ser bonito: necesita ser el mismo en los dos lados.</p>
     */
    private static String sanitize(String raw) {
        if (raw == null || raw.isBlank()) return "item";

        String flattened = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();

        String cleaned = flattened.replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");

        return cleaned.isBlank() ? "item" : cleaned;
    }

    /**
     * Escribe un umbral sin la cola decimal que arrastra un float.
     */
    private static String format(float threshold) {
        String text = Float.toString(threshold);
        return text.endsWith(".0") ? text.substring(0, text.length() - 2) : text;
    }
}

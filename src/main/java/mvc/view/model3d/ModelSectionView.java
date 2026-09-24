package mvc.view.model3d;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;

/**
 * Bloque de interfaz para asignar modelos 3D a un item.
 *
 * <p>Es un trozo de vista, no una vista: no navega ni se registra en
 * {@link mvc.view.ViewType}. Existe como pieza suelta porque las vistas de alta y de
 * modificacion de item no comparten ancestro —{@code AbstractAddView} y
 * {@code AbstractModifyView} son ramas distintas— y la alternativa era el mismo bloque
 * escrito dos veces.</p>
 *
 * <p>No toma decisiones: construye controles, expone los suyos y pinta las filas que le
 * manden. Quien decide que etapa se crea, que fichero se anade y en que orden se pintan es
 * {@link mvc.controller.model3d.ModelSectionController}.</p>
 */
public class ModelSectionView extends VBox {

    /** Opcion del desplegable que significa "sin magnitud". Un combo no admite null. */
    public static final String NO_MAGNITUDE = "(ninguna)";

    private final ComboBox<String> magnitudeCombo;
    private final TextField thresholdField;
    private final Button addStageButton;
    private final VBox stagesList;
    private final Label magnitudeHint;

    public ModelSectionView() {
        super(5);

        Label title = new Label("Modelos 3D");
        title.getStyleClass().add("bold-label");

        magnitudeCombo = new ComboBox<>();
        magnitudeCombo.setPromptText("Magnitud que decide la etapa");
        HBox.setHgrow(magnitudeCombo, Priority.ALWAYS);

        magnitudeHint = new Label();
        magnitudeHint.setWrapText(true);

        HBox magnitudeRow = new HBox(10, new Label("Depende de:"), magnitudeCombo);
        magnitudeRow.setAlignment(Pos.CENTER_LEFT);

        stagesList = new VBox(10);
        stagesList.setPadding(new Insets(5, 0, 5, 0));

        ScrollPane scroll = new ScrollPane(stagesList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(180);

        thresholdField = new TextField();
        thresholdField.setPromptText("Umbral (0 = por defecto)");
        thresholdField.setPrefWidth(160);

        addStageButton = new Button("Anadir etapa");

        HBox inputRow = new HBox(10, thresholdField, addStageButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        this.setPadding(new Insets(10, 0, 0, 0));
        this.getChildren().addAll(title, magnitudeRow, magnitudeHint, scroll, inputRow);
    }

    public ComboBox<String> getMagnitudeCombo() { return this.magnitudeCombo; }
    public TextField getThresholdField() { return this.thresholdField; }
    public Button getAddStageButton() { return this.addStageButton; }

    /**
     * Rellena el desplegable de magnitudes, anteponiendo siempre la opcion de no elegir
     * ninguna.
     *
     * @param magnitudes nombres cualificados {@code Componente.campo}
     */
    public void setAvailableMagnitudes(List<String> magnitudes) {
        magnitudeCombo.getItems().setAll(NO_MAGNITUDE);
        magnitudeCombo.getItems().addAll(magnitudes);
    }

    /**
     * Magnitud elegida, o null si no hay ninguna.
     */
    public String getSelectedMagnitude() {
        String selected = magnitudeCombo.getValue();
        return selected == null || NO_MAGNITUDE.equals(selected) ? null : selected;
    }

    public void setSelectedMagnitude(String magnitude) {
        magnitudeCombo.setValue(magnitude != null ? magnitude : NO_MAGNITUDE);
    }

    /**
     * Texto que explica el estado de la magnitud. Lo escribe el controlador porque depende
     * de cuantas etapas haya, que es algo que esta vista no sabe.
     */
    public void setMagnitudeHint(String hint) {
        magnitudeHint.setText(hint != null ? hint : "");
    }

    public void setMagnitudeEnabled(boolean enabled) {
        magnitudeCombo.setDisable(!enabled);
    }

    public void clearThresholdField() {
        thresholdField.clear();
    }

    public void clearStageRows() {
        stagesList.getChildren().clear();
    }

    /**
     * Pinta una etapa con sus variantes.
     *
     * <p>El umbral se muestra pero no se edita. Cambiarlo sobre la marcha obligaria a
     * reordenar y a revalidar duplicados con cada tecla, y una etapa mal escrita se corrige
     * igual de rapido quitandola y volviendola a anadir.</p>
     *
     * @param stage etapa a pintar
     * @param onAddFile accion al pedir una variante nueva
     * @param onRemoveFile accion al quitar una variante concreta
     * @param onRemoveStage accion al quitar la etapa entera
     */
    public void addStageRow(
            ItemModelStage stage,
            Runnable onAddFile,
            Consumer<ModelFile> onRemoveFile,
            Runnable onRemoveStage
    ) {
        Label thresholdLabel = new Label("Umbral " + stage.getThreshold());
        thresholdLabel.getStyleClass().add("bold-label");

        Button removeStageBtn = new Button("Quitar etapa");
        removeStageBtn.setOnAction(e -> onRemoveStage.run());

        Button addFileBtn = new Button("Anadir variante");
        addFileBtn.setOnAction(e -> onAddFile.run());

        HBox header = new HBox(10, thresholdLabel, addFileBtn, removeStageBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox filesBox = new VBox(5);
        for (ModelFile file : stage.getFiles()) {
            Label fileLabel = new Label(file.toString());

            Button removeFileBtn = new Button("Quitar");
            removeFileBtn.setOnAction(e -> onRemoveFile.accept(file));

            HBox fileRow = new HBox(10, fileLabel, removeFileBtn);
            fileRow.setAlignment(Pos.CENTER_LEFT);
            filesBox.getChildren().add(fileRow);
        }

        if (stage.isEmpty()) {
            filesBox.getChildren().add(new Label("Sin variantes: esta etapa no se exportara."));
        }

        VBox stageBox = new VBox(5, header, filesBox);
        stageBox.getStyleClass().add("component-box");
        stageBox.setUserData(stage);

        stagesList.getChildren().add(stageBox);
    }
}

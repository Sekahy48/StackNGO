package mvc.view.modify;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mvc.view.AbstractView;
import utilities.ImageUtils; 

import dataTransportLayer.EntryDTO;
import identificators.EntryId;

public class AbstractModifyView<T extends EntryDTO> extends AbstractView {

   // ===== CAMPOS COMUNES (FX) =====
   protected EntryId entryId;
   //protected T dto;
   protected Label currentNameLabel;
   protected TextField newNameField;

   protected TextArea currentDescArea;
   protected TextArea newDescArea;

   protected ImageView currentIconView;
   protected Button suggestIconBtn;
   protected String selectedIconPath;

   protected Button confirmBtn;

   @Override
   protected void build() {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /* =========================
           IZQUIERDA: NOMBRE + DESC
           ========================= */

        currentNameLabel = new Label("Nombre actual");
        currentNameLabel.setMinHeight(30);

        newNameField = new TextField();
        newNameField.setPromptText("Nuevo nombre");

        VBox nameBox = new VBox(5, currentNameLabel, newNameField);

        currentDescArea = new TextArea("Descripción actual");
        currentDescArea.setEditable(false);
        currentDescArea.setWrapText(true);

        newDescArea = new TextArea();
        newDescArea.setPromptText("Nueva descripción");
        newDescArea.setWrapText(true);

        VBox descBox = new VBox(5, currentDescArea, newDescArea);
        descBox.setPrefHeight(250);

        VBox leftBox = new VBox(20, nameBox, descBox);

        /* =========================
           DERECHA: ICONO
           ========================= */

        currentIconView = new ImageView();
        currentIconView.setFitWidth(120);
        currentIconView.setFitHeight(120);
        currentIconView.setPreserveRatio(true);

        StackPane iconPane = new StackPane(currentIconView);
        iconPane.setPrefSize(140, 140);
        iconPane.setStyle("-fx-border-color: gray;");

        suggestIconBtn = new Button("Sugerir icono");
        suggestIconBtn.setPrefSize(140, 40);

        VBox iconBox = new VBox(15, iconPane, suggestIconBtn);
        iconBox.setAlignment(Pos.TOP_CENTER);

        /* =========================
           GRID CENTRAL
           ========================= */

        GridPane centerGrid = new GridPane();
        centerGrid.setHgap(40);
        centerGrid.setVgap(20);

        centerGrid.add(leftBox, 0, 0);
        centerGrid.add(iconBox, 1, 0);

        root.setCenter(centerGrid);

        /* =========================
           CONFIRMAR
           ========================= */

        confirmBtn = new Button("Guardar");
        confirmBtn.setPrefWidth(120);

        HBox bottomBox = new HBox(confirmBtn);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));

        root.setBottom(bottomBox);

        this.root.getChildren().add(root);
        addExtraContent(this.root);
    }

   /**
    * Hook para que vistas hijas añadan contenido extra tras el layout base
    * (p.ej. listas editables especificas de la entidad).
    */
   protected void addExtraContent(VBox root) {
        // No-op por defecto
   }

   public void clear(){
      newNameField.clear();
      newDescArea.clear();
      selectedIconPath = null;
      suggestIconBtn.setGraphic(null);
   }

   // ===== MÉTODO DE RELLENO =====
   public void modifyFields(T dto) {
      this.entryId = new EntryId(dto.id); 
      this.currentNameLabel.setText(dto.name);
      this.currentDescArea.setText(dto.description); 
      Image icon = ImageUtils.getImage(dto.imagePath);
      this.currentIconView.setImage(icon);

      this.suggestIconBtn.setGraphic(null);
      this.suggestIconBtn.setText("Sugerir icono");

   }

   public Button getConfirmButton(){
      return this.confirmBtn;
   }

   public Button getIconPreviewButton(){
      return this.suggestIconBtn;
   }

   public String getNewName(){
      return newNameField.getText();
   }

   public String getNewDescription(){
      return newDescArea.getText();
   }

   public String getNewImagePath(){
      return selectedIconPath;
   }

   public int getEntryId(){
      return entryId.value();
   }

   public EntryId getFullEntryId(){
      return entryId;
   }
  
 
   public void setIconPreview(Image image) {
    ImageView iv = new ImageView(image);
      iv.setFitWidth(32);
      iv.setFitHeight(32);
      iv.setPreserveRatio(true);

      suggestIconBtn.setGraphic(iv);
      suggestIconBtn.setText("");
}

   public void setSelectedIconPath(String path) {
      this.selectedIconPath = path;
   }

   public void fillLabels(String name, String description, String iconPath) {
      this.currentNameLabel.setText(name);
      this.currentDescArea.setText(description);
      if (iconPath != null && !iconPath.isEmpty()) this.currentIconView.setImage(ImageUtils.getImage(iconPath));
   }

}
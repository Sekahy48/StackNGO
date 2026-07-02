package mvc.view.add;
 
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import mvc.view.AbstractView;

public abstract class AbstractAddView extends AbstractView {
    // Campos comunes
    protected TextField nameField;
    protected TextArea descriptionArea;
    protected ImageView preview;
    protected Label fileNameLabel;
    protected Button imageButton;
    protected Button goBackButton;
    protected Button addButton;
    protected Label nameLabel;
    protected Label iconLabel;
    protected Label descriptionLabel;

    protected AbstractAddView() {
        super();
    }

    public TextField getNameLabel() { return this.nameField; }
    public Label getIconLabel() { return this.fileNameLabel; }
    public TextArea getDescriptionLabel() { return this.descriptionArea; }
    public Button getGoBackButton() { return this.goBackButton; }
    public Button getImageButton() { return this.imageButton; }
    public Button getAddButton () { return this.addButton; }

    @Override
    protected void build() {
        this.nameLabel = new Label();
        this.addButton = new Button("Botón de añadir");

        buildSpecificFields();
        
        this.iconLabel = new Label("Icono:");
        this.descriptionLabel = new Label("Descripcion:");

        this.nameField = new TextField();
        this.descriptionArea = new TextArea();

        this.preview = new ImageView();
        this.preview.setFitWidth(40);
        this.preview.setFitHeight(40);
        this.preview.setPreserveRatio(true);

        this.fileNameLabel = new Label();
        this.imageButton = new Button("Añadir imagen");
        this.goBackButton = new Button("Cancelar");
        
        
        HBox imageBox = new HBox(10, imageButton, preview, fileNameLabel);
        HBox buttonBox = new HBox(10, this.addButton, goBackButton);
        buttonBox.setStyle("-fx-alignment: center-right;");
        root.setPadding(new Insets(15));
        root.setSpacing(10);
        root.getChildren().addAll(
                nameLabel, nameField,
                iconLabel, imageBox,
                descriptionLabel, descriptionArea
        );

        addExtraContent(root);

        root.getChildren().add(buttonBox);
    }

    /**
     * Hook para que las vistas hijas añadan contenido extra entre la descripcion
     * y la fila de botones (añadir/cancelar), sin tener que reconstruir el layout base.
     * Por defecto no añade nada.
     *
     * @param root VBox raiz de la vista, ya con nombre/icono/descripcion añadidos
     */
    protected void addExtraContent(VBox root) {
        // No-op por defecto
    }

    public void setImage(File file) {
        fileNameLabel.setText(file.getAbsolutePath());
        preview.setImage(new Image(file.toURI().toString()));
    }

    protected abstract void buildSpecificFields();

    public void clearFields() {
        nameField.clear();
        descriptionArea.clear();
        fileNameLabel.setText("");
    }
}
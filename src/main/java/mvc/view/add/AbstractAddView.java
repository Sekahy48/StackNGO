package mvc.view.add;

import creational.UIPrefabsFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @Override
    protected void build() {

        this.nameLabel = new Label();
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
        buildSpecificFields();
    }

    public void setImage(File file) {
        fileNameLabel.setText(file.getAbsolutePath());
        preview.setImage(new Image(file.toURI().toString()));
    }

    protected abstract void buildSpecificFields();
}
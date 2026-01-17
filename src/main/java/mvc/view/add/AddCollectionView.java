package mvc.view.add;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 *
 * View that shows what the user sees when adding a new {@code Collection} to their account
 *
 */
public class AddCollectionView extends AbstractAddView {

    private Button addButton;

    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public AddCollectionView() {
        super();
    }

    /**
     *
     * Method that gets the button used to add the new {@code Collection}
     *
     * @return button
     */
    public Button getAddButton () { return this.addButton; }

    @Override
    protected void buildSpecificFields() {

        this.nameLabel.setText("Nombre de la coleccion");

        HBox imageBox = new HBox(10, imageButton, preview, fileNameLabel);

        this.addButton = new Button("Añadir coleccion");
        HBox buttonBox = new HBox(10, this.addButton, goBackButton);
        buttonBox.setStyle("-fx-alignment: center-right;");

        root.setPadding(new Insets(15));
        root.setSpacing(10);
        root.getChildren().addAll(
                nameLabel, nameField,
                iconLabel, imageBox,
                descriptionLabel, descriptionArea,
                buttonBox
        );
    }
}

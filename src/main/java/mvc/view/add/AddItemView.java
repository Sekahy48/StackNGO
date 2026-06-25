package mvc.view.add;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import mvc.model.entries.Entry;

/**
 *
 * View that shows what the user sees when adding a new {@code Item} to their account
 *
 */
public class AddItemView extends AbstractAddView { 

    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public AddItemView() {
        super();
    }

    /**
     *
     * Method that returns the button used to add the new {@code Item}
     *
     * @return the button used to add the new {@link Entry}
     */
    public Button getAddButton() { return this.addButton; }

    @Override
    protected void buildSpecificFields() {

        this.nameLabel.setText("Nombre del item");

        HBox imageBox = new HBox(10, imageButton, preview, fileNameLabel);

        this.addButton = new Button("Añadir item");

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

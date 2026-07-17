package mvc.view.show.multiple;

import dataTransportLayer.ItemWithCollectionDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane; 

public class ShowRecipesView extends ShowGridDisplayView<ItemWithCollectionDTO> {

    public ShowRecipesView() {
        super();
    }

    @Override
    protected void build() {

    }

    @Override
    protected Button createElementCard(ItemWithCollectionDTO dto){
        return this.createRecipeCard(dto.getImagePath(), dto.getName(), dto.collection);
    }
    /**
 * Crea una "card" con imagen, título inferior y nombre de colección superior.
 * @param imagePath Ruta de la imagen (puede ser null)
 * @param title Título principal (label inferior)
 * @param collection Nombre de la colección (label superior)
 * @return Button con la card
 */
    private Button createRecipeCard(String imagePath, String title, String collection) {
        ImageView imageView;
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView = new ImageView(new Image(imagePath));
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
        } else {
            imageView = new ImageView();
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.getStyleClass().add("card-placeholder");
        }

        // Label inferior (title)
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Label superior (collection)
        Label collectionLabel = new Label(collection);
        collectionLabel.getStyleClass().add("card-collection");
        collectionLabel.setMaxWidth(Double.MAX_VALUE);

        // StackPane con imagen y labels
        StackPane stack = new StackPane();
        stack.getChildren().addAll(imageView, titleLabel, collectionLabel);
        StackPane.setAlignment(titleLabel, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(collectionLabel, Pos.TOP_CENTER);

        // Botón clicable
        Button cardButton = new Button();
        cardButton.setGraphic(stack);
        cardButton.getStyleClass().add("card-button");

        return cardButton;
    }

}

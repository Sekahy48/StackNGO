package mvc.view.show.single;

import javafx.geometry.Pos;
import javafx.scene.control.Label; 
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AbstractShowEntryView extends AbstractShowDataView {

    public AbstractShowEntryView() {
        super();
    }

    @Override
    protected void build() {
        super.build();
    }

    @Override
    protected void buildFields() {
        Label nameLabel = this.entryName;
        nameLabel.getStyleClass().add("entry-name");

        Label descriptionLabel = this.description;
        descriptionLabel.getStyleClass().add("entry-description");

        VBox textContainer = new VBox(10);
        textContainer.getChildren().addAll(nameLabel, descriptionLabel);
        textContainer.setAlignment(Pos.TOP_LEFT);

        this.entryIcon = new ImageView();
        this.entryIcon.setFitHeight(100);
        this.entryIcon.setFitWidth(100);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(textContainer);
        borderPane.setRight(entryIcon);

        this.root.getChildren().addAll(borderPane);
    }
}
package mvc.view.show.entry.data;

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
        nameLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 10px; -fx-font-size: 16px; -fx-border-color: black; -fx-border-width: 1px;");

        Label descriptionLabel = this.description;
        descriptionLabel.setStyle("-fx-background-color: lightgreen; -fx-padding: 10px; -fx-font-size: 14px; -fx-border-color: black; -fx-border-width: 1px;");

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
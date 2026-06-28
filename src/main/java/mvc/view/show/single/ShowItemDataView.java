package mvc.view.show.single;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ShowItemDataView extends AbstractShowEntryView {

    private VBox centralPanel, rightPanel;
    private ImageView plusIconR, plusIconI;
    private HBox actionButtonHBox;
 

    public ShowItemDataView() {
        super();
    }

    public Button getGoBackButton() { return this.goBackButton; }

    @Override
    protected void buildFields() {
        build();
    }

    @Override
    protected void build() {
        super.build();
        
        this.root = new BorderPane();
        this.root.setPadding(new Insets(15));

        //this.goBackIcon = new ImageView(new Image("images/volver.png"));
        this.goBackIcon.setFitHeight(32);
        this.goBackIcon.setFitWidth(32);

        this.goBackButton = new Button();
        this.goBackButton.setGraphic(this.goBackIcon);
        this.goBackButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 2;"
        );

        this.plusIconR = new ImageView(new Image("images/añadir.png"));
        this.plusIconR.setFitHeight(16);
        this.plusIconR.setFitWidth(16);

        this.addRecipeButton = new Button();
        this.addRecipeButton.setGraphic(this.plusIconR);
        this.addRecipeButton.setStyle("-fx-background-color: transparent;");

        this.plusIconI = new ImageView(new Image("images/añadir.png"));
        this.plusIconI.setFitHeight(16);
        this.plusIconI.setFitWidth(16);

        this.addItemButton = new Button();
        this.addItemButton.setGraphic(this.plusIconI);
        this.addItemButton.setStyle("-fx-background-color: transparent;");

        this.centralPanel = new VBox(10);
        this.centralPanel.setPadding(new Insets(10, 20, 10, 20));
        this.centralPanel.setAlignment(Pos.CENTER);

        this.nameField = new TextField();
        this.nameField.setEditable(false);
        this.nameField.setFocusTraversable(false);

        this.descriptionArea = new TextArea();
        this.descriptionArea.setEditable(false);
        this.descriptionArea.setPrefRowCount(10);

        // Botones para eliminar, editar y volver atras

        this.modifyIcon = new ImageView(new Image("images/lapiz.png"));
        this.modifyIcon.setFitHeight(32);
        this.modifyIcon.setFitWidth(32);

        this.modifyButton = new Button();
        this.modifyButton.setGraphic(this.modifyIcon);
        this.modifyButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;"
        );

        this.deleteIcon = new ImageView(new Image("images/papelera.png"));
        this.deleteIcon.setFitHeight(32);
        this.deleteIcon.setFitWidth(32);

        this.deleteButton = new Button();
        this.deleteButton.setGraphic(this.deleteIcon);
        this.deleteButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;"
        );

        // HBox que contiene a los dos botones
        this.actionButtonHBox = new HBox(10, goBackButton, modifyButton, deleteButton);
        this.actionButtonHBox.setPadding(new Insets(10, 0, 0, 0));
        this.actionButtonHBox.setStyle("-fx-alignment: center-right;");

        this.centralPanel.getChildren().addAll(
                nameField,
                descriptionArea,
                this.actionButtonHBox
        );

        this.root.setCenter(centralPanel);

        // Panel derecho donde está el icono de la colección

        this.rightPanel = new VBox();
        this.rightPanel.setMinWidth(100);
        this.rightPanel.setPadding(new Insets(10));
        this.rightPanel.setAlignment(Pos.CENTER);

        this.entryIcon = new ImageView();
        this.entryIcon.setFitWidth(200);
        this.entryIcon.setFitHeight(200);
        this.entryIcon.setPreserveRatio(true);

        this.rightPanel.getChildren().add(entryIcon);

        this.root.setRight(rightPanel);
    }
}
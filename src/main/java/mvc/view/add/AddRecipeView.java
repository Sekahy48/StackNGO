package mvc.view.add;
 
import creational.UIPrefabsFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddRecipeView extends AbstractAddView {


    private Button addButton, addIngredientButton, addResultButton;
    private VBox ingredientsList;
    private VBox resultsList;
    private VBox ingredients;
    private VBox results;
    private HBox listContainer;
    private ImageView plusView1, plusView2;

    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public AddRecipeView() {
        super();
    }

    public Button getAddButton() { return this.addButton; }
    public Button getAddIngredientButton() { return this.addIngredientButton; }
    public Button getAddResultButton() { return this.addResultButton; }
    public VBox getIngredientsList() { return this.ingredients; }
    public VBox getResultsList() { return this.results; }

    @Override
    protected void buildSpecificFields() {

        this.nameLabel.setText("Nombre de la receta");

        HBox imageBox = new HBox(10, imageButton, preview, fileNameLabel);

        this.ingredientsList = new VBox(5);
        this.resultsList = new VBox(5);

        this.plusView1 = new ImageView(new Image("images/añadir.png"));
        this.plusView1.setFitWidth(20);
        this.plusView1.setFitHeight(20);

        this.plusView2 = new ImageView(new Image("images/añadir.png"));
        this.plusView2.setFitWidth(20);
        this.plusView2.setFitHeight(20);

        this.addIngredientButton = new Button();
        this.addIngredientButton.setGraphic(plusView1);
        this.addIngredientButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #c0c0c0;" +
            "-fx-border-width: 2;"
        );
        this.addIngredientButton.setMaxWidth(Double.MAX_VALUE);

        this.addResultButton = new Button();
        this.addResultButton.setGraphic(plusView2);
        this.addResultButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #c0c0c0;" +
            "-fx-border-width: 2;"
        );
        this.addResultButton.setMaxWidth(Double.MAX_VALUE);

        this.ingredients = new VBox(5);
        this.ingredientsList = UIPrefabsFactory.createScrollableModifableList(
            new Label("Ingredientes"),
            ingredients,
            addIngredientButton
        );

        VBox.setVgrow(addIngredientButton, javafx.scene.layout.Priority.NEVER);

        this.results =  new VBox(5);
        this.resultsList = UIPrefabsFactory.createScrollableModifableList(
             new Label("Resultados"),
             results,
             addResultButton
        );

        VBox.setVgrow(addResultButton, javafx.scene.layout.Priority.NEVER);

        this.listContainer = new HBox(20, ingredientsList, resultsList);

        this.addButton = new Button("Añadir receta");

        HBox buttonBox = new HBox(10, this.addButton, goBackButton);
        buttonBox.setStyle("-fx-alignment: center-right;");

        root.setPadding(new Insets(15));
        root.setSpacing(10);
        root.getChildren().addAll(
                nameLabel, nameField,
                iconLabel, imageBox,
                descriptionLabel, descriptionArea,
                listContainer, buttonBox
        );
    }
}

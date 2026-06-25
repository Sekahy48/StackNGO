package mvc.view.show;

import dataTransportLayer.GenericDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane; 
import mvc.view.AbstractView;

import java.io.File;
 

public abstract class ShowGridDisplayView<T extends GenericDTO> extends AbstractView {
    protected GridPane contentGrid;
    protected String title; 

    public ShowGridDisplayView(){
        super();
    }

    /**
     * Compone la view con la siguiente estructura
     * Arriba una barra con "Genero de lo contenido" y el nombre de la cuenta vigente
     * En el resto, una grid de botones de 5 columnas con por cada boton una coleccion
     * mostrando nombre/titulo e imagen.
     */
    @Override
    protected void build() {

        // Contenedor principal (ya existe como VBox en AbstractView)
        root.setSpacing(15);


        // ----- Contenedor inferior (rejilla de entidades) -----
        this.contentGrid = new GridPane();
        contentGrid.setHgap(15);
        contentGrid.setVgap(15);
        contentGrid.setPadding(new Insets(20));

        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setFillWidth(true);
            contentGrid.getColumnConstraints().add(col);
        }

        ScrollPane scrollPane = new ScrollPane(contentGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
  
        this.initSidebar(this.contentGrid);
        
    }

    protected Button createElementCard(T dto){
        return this.createGenericElementCard(dto.getImagePath(), dto.getName());
    }
    /**
     * Metodo privado que construye una de las celdas/boton de la grid de esta vista
     * Si la imagen es null, se añade una imagen por defecto
     * @param imagePath
     * @param title
     * @return
     */
    private Button createGenericElementCard(String imagePath, String title) {

        ImageView imageView;

        if (imagePath != null && !imagePath.isEmpty()) {
            Image image;

            if (imagePath.startsWith("file:")) {
                image = new Image(imagePath);
            } else {
                File file = new File(imagePath);
                image = new Image(file.toURI().toString());
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(false);
        } else {
            imageView = new ImageView();
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setStyle("-fx-background-color: #888888;");
        }

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-background-color: rgba(0,0,0,0.6);" +
            "-fx-text-fill: white;" +
            "-fx-alignment: center;" +
            "-fx-padding: 5;"
        );
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(imageView, titleLabel);
        StackPane.setAlignment(titleLabel, Pos.BOTTOM_CENTER);

        Button cardButton = new Button();
        cardButton.setGraphic(stack);
        cardButton.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

        return cardButton;
    }
 
    /**
     * Metodo para, desde fuera, añadir un elemento a mostrar en la grid.
     * Este elemento se añade en el siguiente hueco libre.
     */
    public void  addElementToGrid(T dto, EventHandler<ActionEvent> onClick) {
        Button card = this.createElementCard(dto);

        int total = this.contentGrid.getChildren().size();
        int col = total % 5;
        int row = total / 5;
        card.setOnAction(onClick);
        this.contentGrid.add(card, col, row);
    }

    public void emptyGrid() {
        this.contentGrid.getChildren().clear();
    }

   
}

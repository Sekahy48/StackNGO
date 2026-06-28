package mvc.view.inventory;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class InventoryView extends AbstractInventoryView {

    // Contexto
    private Button selectCollectionButton;
    private Button selectRecipeButton;


    @Override
    protected void build() {
        selectCollectionButton = new Button("Seleccionar colección");
        selectCollectionButton.setMaxWidth(Double.MAX_VALUE);

        selectRecipeButton = new Button("Seleccionar receta");
        selectRecipeButton.setMaxWidth(Double.MAX_VALUE);
        super.build(); 
    }

       
    @Override
    protected VBox buildRightPanel() {
        VBox rightPanel = super.buildRightPanel();
        // Insertar botones después de sus labels respectivos
        rightPanel.getChildren().add(1, selectCollectionButton); // después de selectedCollectionLabel
        rightPanel.getChildren().add(3, selectRecipeButton);     // después de selectedRecipeLabel
        return rightPanel;
    }

    //#region Getters
    
    public Button getSelectCollectionButton() {
        return this.selectCollectionButton;
    }

    public Button getSelectRecipeButton() {
        return this.selectRecipeButton;
    }

    @Override
    protected void attachContentToRoot() {
        this.initSidebar(contentContainer); // añade via sidebar, no directo
    }
}

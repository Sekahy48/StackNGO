package mvc.view;

import java.util.Optional;

import dataTransportLayer.EventBuffer;
import identificators.GenericId;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;


/**
 *
 * Abstract class that holds all the common elements among the views
 *
 */
public abstract class AbstractView {

    protected VBox root;
    protected EventBuffer buffer;
    protected GenericId parentId;
    protected String parentName;
 
    protected Button userButton, collectionButton, inventoryButton;

    protected VBox sideBar;
    protected SplitPane splitPane;
    

    /**
     *
     * Constructor that receives an {@link EventBuffer} where events are placed
     *
     */
    public AbstractView() {

        this.root = new VBox(10);

        this.userButton = new Button();
        this.collectionButton = new Button();
        this.inventoryButton = new Button();
        this.sideBar = new VBox(10);
        this.splitPane = new SplitPane();
        this.build();
    }

    public Button getUserButton() { return this.userButton; }

    public Button getCollectionButton() { return this.collectionButton; }

    public Button getInventoryButton() { return this.inventoryButton; }

    public SplitPane getSplitPane() { return this.splitPane;}
    /**
     *
     * Method that initializes this class' atributes
     *
     */
    protected abstract void build();

    /**
     *
     * Method that returns the {@code Root}
     *
     * @return root
     */
    public Parent getRoot() {
        return root;
    }

    /**
     *
     * @param title serves as the alert title
     * @param message provides information about the action
     * @return whether you agree or not
     */
    public boolean showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void setParentId(GenericId id){
        this.parentId = id;
    }

    public void setParentName(String name){
        this.parentName = name;
    }

    public GenericId getParentId(){
        return this.parentId;
    }

    public String getParentName(){
        return this.parentName;
    }
}
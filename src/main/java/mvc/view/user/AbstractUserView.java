package mvc.view.user;

import dataTransportLayer.EventBuffer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mvc.view.AbstractView;

/**
 *
 * Abstract class that holds common components among the views that extend this
 *
 */
public abstract class AbstractUserView extends AbstractView {

    protected Label title;
    protected HBox hBox;
    protected Button confirmButton;
    
    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public AbstractUserView() {
        super();
    }

    @Override
    protected void build() {
        this.title = new Label();
        this.hBox = new HBox();
        this.confirmButton = new Button();
        buildFields();
    }

    /**
     *
     * Method that initializes the class that extends from this and
     *
     */
    protected abstract void buildFields();
}
package mvc.view.show.entry.data;

import identificators.GenericId;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import mvc.view.AbstractView;

public abstract class AbstractShowDataView extends AbstractView {

    protected BorderPane root;
    protected Label entryName;
    protected TextField nameField;
    protected Label description;
    protected TextArea descriptionArea;
    protected ImageView entryIcon;
    protected Button modifyButton;
    protected Button deleteButton;
    protected Button addRecipeButton;
    protected Button addItemButton;
    protected ImageView modifyIcon;
    protected ImageView deleteIcon;

    protected GenericId parentId;
    protected String parentName;

    public AbstractShowDataView() {
        super();
    }

    public Button getAddRecipeButton() { return this.addRecipeButton; }
    public Button getAddItemButton() { return this.addItemButton; }
    public Button getModifyButton() { return this.modifyButton; }
    public Button getDeleteButton() { return this.deleteButton; }
    public ImageView getEntryIcon() {
        return this.entryIcon;
    }
    public TextField getNameField() {
        return this.nameField;
    }
    public TextArea getDescriptionArea() {
        return this.descriptionArea;
    }

    public BorderPane getRoot() {
        return root;
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

    protected abstract void buildFields();
}
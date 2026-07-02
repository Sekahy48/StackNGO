package mvc.view.add;  

/**
 * Class that represents the view for adding a new item to the user's account.
 * AddItemView
 */
public class AddItemView extends AbstractAddView {  
 
    @Override
    protected void buildSpecificFields() {
        this.nameLabel.setText("Nombre del item");
        this.addButton.setText("Añadir item");
    }
}

package mvc.view.add;
  

/**
 * Class that represents the view for adding a new collection to the user's account.
 * AddCollectionView
 */
public class AddCollectionView extends AbstractAddView { 

    @Override
    protected void buildSpecificFields() { 
        this.nameLabel.setText("Nombre de la colección"); 
        this.addButton.setText("Añadir colección");
    }
}

package mvc.controller.inventory;
 
import mvc.view.inventory.InventoryPopupView;
public class InventoryPopupController extends AbstractInventoryController<InventoryPopupView> {

    @Override
    public void attachView(InventoryPopupView view) {
        super.attachView(view);
        populateGrid();
    }
}

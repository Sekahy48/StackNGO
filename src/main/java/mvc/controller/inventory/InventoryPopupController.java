package mvc.controller.inventory;

import dataTransportLayer.EventBuffer;
import mvc.view.inventory.InventoryPopupView;
public class InventoryPopupController extends AbstractInventoryController<InventoryPopupView> {

    public InventoryPopupController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(InventoryPopupView view) {
        super.attachView(view);
        populateGrid();
    }
}

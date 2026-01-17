package mvc.view.inventory;

import dataTransportLayer.ItemDTO;
import mvc.model.inventory.IInventoryElement;

public class InventoryCellData {
    public final ItemDTO itemDTO;
    public final IInventoryElement inventoryElement;

    public InventoryCellData(ItemDTO itemDTO, IInventoryElement inventoryElement) {
        this.itemDTO = itemDTO;
        this.inventoryElement = inventoryElement;
    }
}

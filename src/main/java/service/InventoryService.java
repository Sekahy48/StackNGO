package service;
 
import mvc.context.SessionContext;
import mvc.model.entries.Item;
import mvc.model.inventory.IInventoryElement;

public class InventoryService implements IService{

    private SessionContext context;

    @Override
    public ServiceType getType() {
        return ServiceType.INVENTORY;
    }

    public IInventoryElement getCurrentInventory() {
        return this.context.getInventoryStack().peek();
    }

    public IInventoryElement returnToParentInventory() {
        return this.context.getInventoryStack().pop();
    }

    public void clearCurrentInventory() {
        this.getCurrentInventory().clearInventory();
    }

    public void pushCurrentInventory(IInventoryElement newInventory) {
        this.context.getInventoryStack().push(newInventory);
    }
    
    /**
     * Comprueba si el inventario actual contiene
     * @param item
     * @return
     */
    public boolean containsAsContainer(Item item) {
        IInventoryElement found = getCurrentInventory().findHere(item);
        return found != null && !found.isLeaf();
    }
}

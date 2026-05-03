package service;
 
import mvc.context.SessionContext;

public class InventoryService implements IService{

    private SessionContext context;

    @Override
    public ServiceType getType() {
        return ServiceType.INVENTORY;
    }

    /**
     * Comprueba si el inventario actual contiene
     * @param item
     * @return
     */
    public boolean containsAsContainer(Item item) {

    }

}

package mvc.controller.show.multiple;
 
import java.util.List;
 
import dataTransportLayer.ItemDTO;
import service.ItemService;
import service.ServiceType;
import service.SessionService;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowItemsController extends ShowGridDisplayController<ItemDTO> {

    @Override
    protected List<ItemDTO> getElements() {
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return itemService.getAllDTO(sessionService.getCurrentCollectionDTO().id);
    } 

    @Override
    protected void onClickElementEvent(ItemDTO dto) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        sessionService.setCurrentItem(dto);
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    

}
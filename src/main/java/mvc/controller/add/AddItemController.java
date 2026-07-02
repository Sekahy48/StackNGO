package mvc.controller.add;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import creational.DTOFactory; 
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO; 
import event.EventBus;
import event.NavigateEvent; 
import javafx.scene.control.Button; 
import mvc.controller.InyectableController;
import mvc.model.entries.Item;
import mvc.view.ViewType; 
import mvc.view.add.AddItemView; 
import service.ItemService;
import service.ServiceType;
import service.SessionService; 

/**
 *
 * Controller that manages the logic related to {@link AddItemView}
 *
 */
public class AddItemController extends AbstractAddController<ItemDTO, Item, AddItemView> implements InyectableController{
    protected List<EntryDTO> listWhereAdd; 
    
    public void setListWhereAdd(List<EntryDTO> list){
        listWhereAdd = list;
    }
 

    public void onReturnEvent() {
        System.out.println("Returning to SHOW_COLLECTION");
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }
    
   

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.ITEM);
        return out;
    }

    @Override
    public ItemService getEntryService() {
        return this.<ItemService>getService(ServiceType.ITEM);
    }

    @Override
    public String getEntryType() { 
        return "El item";
    }

    @Override
    public ItemDTO getDTOFromView() {
        String name = this.view.getNameLabel().getText();
        String iconLabel = this.view.getIconLabel().getText();
        String description = this.view.getDescriptionLabel().getText();
        
        ItemDTO dto = DTOFactory.item(
                        name,
                        iconLabel,
                        description,
                        this.idGenerator.generateId()
        );

        return dto;
    }

    @Override
    public int getParentId() {
        return this.<SessionService>getService(ServiceType.SESSION).getCurrentCollectionDTO().id;
    }
}
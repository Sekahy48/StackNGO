package mvc.controller.show;
 
import java.util.List;

import command.ICommand;
import command.screen.ChangeToCommand;
import command.screen.RedirectCommand;
import command.show.ShowItem;
import command.show.ShowItems;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemWithCollectionDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mvc.view.ViewType;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowItemsController extends ShowGridDisplayController<ItemWithCollectionDTO> {

    public ShowItemsController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    protected List<ItemWithCollectionDTO> getElements() {
        return this.context.getItems();
    }

    @Override
    protected String getTitle(ItemWithCollectionDTO dto) {
        return dto.item.name;
    }

    @Override
    protected String getImagePath(ItemWithCollectionDTO dto) {
        return dto.item.iconPath; // accounts no tienen imagen
    }

    @Override 
    protected RedirectCommand createCommand(ItemWithCollectionDTO dto) {

        ICommand back = new RedirectCommand(buffer, new ShowItems());

        ICommand forward = new ShowItem(dto.item.id, back);

        return new RedirectCommand(
            context.getSystemContext().getController(ViewType.SHOW_ITEM).getBuffer(),
            forward
        );
    }

    @Override
    protected EventHandler<ActionEvent> createEventHandler(ItemWithCollectionDTO element) {
         
        return (e -> { 

            buffer.publish(new RedirectCommand(this.context.getSystemContext().getController(ViewType.SHOW_ITEM).getBuffer(), 
                           new ChangeToCommand(ViewType.SHOW_ITEMS, element.item)));
            buffer.publish(createCommand(element));
            context.getSessionContext().setCurrentCollection(context.getCollectionByName(element.collection));
        });
    }

}
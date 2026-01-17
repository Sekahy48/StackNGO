package mvc.controller.show;

import java.util.ArrayList;
import java.util.List;

import command.screen.RedirectCommand;
import command.show.ShowCollection;
import command.show.ShowItem;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemWithCollectionDTO;

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
        return new RedirectCommand(this.context.getCoreController().getShowItemDataBuffer(), new ShowItem(dto.item.id));
    }
}
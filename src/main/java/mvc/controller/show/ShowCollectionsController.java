package mvc.controller.show;


import java.util.List;

import command.screen.RedirectCommand;
import command.show.ShowCollection;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EventBuffer;
import mvc.view.ViewType;

/**
 * Controlador para la vista de colecciones.
 */
public class ShowCollectionsController extends ShowGridDisplayController<CollectionDTO> {

    public ShowCollectionsController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    protected List<CollectionDTO> getElements() {
        return context.getCollections();
    }

    @Override
    protected String getTitle(CollectionDTO dto) {
        return dto.name;
    }

    @Override
    protected String getImagePath(CollectionDTO dto) {
        return dto.iconPath;
    }

    @Override
    protected RedirectCommand createCommand(CollectionDTO dto) {

        return new RedirectCommand(this.context.getSystemContext().getController(ViewType.SHOW_COLLECTION).getBuffer(),
                new ShowCollection(dto));
    }
}
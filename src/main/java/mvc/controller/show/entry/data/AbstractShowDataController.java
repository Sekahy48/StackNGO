package mvc.controller.show.entry.data;

import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EventBuffer;
import identificators.EntryId;
import javafx.scene.control.Button;
import mvc.controller.AbstractController;
import mvc.model.entries.Entry;
import mvc.view.ViewType;
import mvc.view.show.entry.data.AbstractShowDataView;

public abstract class AbstractShowDataController<T extends AbstractShowDataView> extends AbstractController<T> {

    protected Button modifyButton;
    protected Button deleteButton;

    public AbstractShowDataController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(T view) {
        this.view =  view;
        super.attachView(view);
    }

    public abstract void handleButton();

    protected abstract void goBack();
}
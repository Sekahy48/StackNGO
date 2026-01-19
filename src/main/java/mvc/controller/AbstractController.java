package mvc.controller;

import command.BackNavigation;
import command.ICommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollections;
import command.show.ShowItems;
import dataTransportLayer.EventBuffer;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.context.RuntimeContext;
import mvc.view.AbstractView;
import mvc.view.ViewType;
import observer.IObserver;

public abstract class AbstractController<T extends AbstractView> implements IObserver<EventBuffer> {

    protected EventBuffer buffer;
    protected RuntimeContext context;
    protected T view;

    protected ICommand backCommand;


    public AbstractController(EventBuffer buffer) {
        this.buffer = buffer;
        buffer.attachObserver(this);
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.context = runtimeContext;
    }

    public abstract void handleButton();

    protected void commonHandleButton() {

        AbstractView view = this.getView();

        Button userButton = view.getUserButton();
        Button collectionButton = view.getCollectionButton();
        Button inventoryButton = view.getInventoryButton();
        Button itemButton = view.getItemButton();
        userButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE)); 
                }
        );

        collectionButton.setOnAction(

                e -> {
                    this.buffer.publish(new RedirectCommand(
                                    this.context.getCoreController().getController(ViewType.SHOW_COLLECTIONS).getBuffer(),
                                    new ShowCollections()
                                 
                            )
                    );
                    this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTIONS)); 
                }
        );

        itemButton.setOnAction(
                e -> {
                    this.buffer.publish(new RedirectCommand(
                                    this.context.getCoreController().getController(ViewType.SHOW_ITEMS).getBuffer(),
                                    new ShowItems()
                                 
                            )
                    );
                    this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_ITEMS)); 
                }

        );

        inventoryButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.INVENTORY)); 
                }
        );
    }

    public T getView() {
        return this.view;
    }

    @Override
    public void update(EventBuffer buffer) {
        //revisar esto
        for (ICommand command : buffer.drain()) {
            command.execute( this);
        }
    }

    public void setBackNavigation(ICommand backCommand) {
        this.backCommand = backCommand;
    }

    public void attachView(T view) {
        this.handleButton(); 
    }

    public EventBuffer getBuffer() {
        return buffer;
    }

    public RuntimeContext getRuntimeContext() {
        return context;
    }

    protected void goBack() {
        if (backCommand != null) {
            this.buffer.publish(backCommand);
            this.backCommand = null;
        } else {
            Logger.getInstance().log(
                LogLevel.WARNING,
                getClass().toString(),
                "goBack sin contexto de navegación"
            );
        }
    }


}

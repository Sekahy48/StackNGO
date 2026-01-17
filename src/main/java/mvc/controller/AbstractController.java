package mvc.controller;

import command.ICommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollections;
import dataTransportLayer.EventBuffer;
import javafx.scene.control.Button;
import mvc.context.RuntimeContext;
import mvc.view.AbstractView;
import mvc.view.ViewType;
import observer.IObserver;

public abstract class AbstractController<T extends AbstractView> implements IObserver<EventBuffer> {

    protected EventBuffer buffer;
    protected RuntimeContext context;
    protected T view;

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
        
        userButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE)); 
                }
        );

        collectionButton.setOnAction(

                e -> {
                    this.buffer.publish(new RedirectCommand(
                                    this.context.getCoreController().getShowCollectionsBuffer(),
                                    new ShowCollections()
                                 
                            )
                    );
                    this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTIONS)); 
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

    public void attachView(T view) {
        this.handleButton(); 
    }

    public EventBuffer getBuffer() {
        return buffer;
    }

    public RuntimeContext getRuntimeContext() {
        return context;
    }
}

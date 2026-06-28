package mvc.controller;
  
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Button; 
import mvc.view.AbstractView;
import mvc.view.ViewType; 
import service.ServiceConsumer;

public abstract class AbstractController<T extends AbstractView> extends ServiceConsumer {
 
    protected T view;
    public abstract void handleButtons();

    /**
     * Method that sets handlers for the buttons of the sidebar common to most of the main views.
     * Buttons: user space, collections list, items list, inventory playgorund.
     */
    protected void commonHandleButton() {

        AbstractView view = this.getView();

        Button userButton = view.getUserButton();
        Button collectionButton = view.getCollectionButton();
        Button inventoryButton = view.getInventoryButton();
        Button itemButton = view.getItemButton();
        Button componentButton = view.getComponentButton();
        userButton.setOnAction(
                e -> {
                    EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE)); 
                }
        );

        collectionButton.setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTIONS));});

        itemButton.setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ITEMS));});

        inventoryButton.setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.INVENTORY));});

        componentButton.setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENTS));});
    }

    public T getView() {
        return this.view;
    } 

    public void attachView(T view) {
        this.handleButtons(); 
    }
 
    public abstract void onReturnEvent();
 
 
    public void updateAtShow(){ 
    }
}

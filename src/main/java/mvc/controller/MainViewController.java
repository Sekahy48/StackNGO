package mvc.controller;
 
import event.EventBus;
import javafx.scene.control.Button;
import mvc.view.MainView;
import mvc.view.ViewType;

/**
 *
 * Controller that manages the logic related to {@link MainView}
 *
 */
public class MainViewController extends AbstractController<MainView> {


    @Override
    public void handleButtons() {

        Button enterButton = this.view.getEnterButton();

        enterButton.setOnAction(
                e -> {
                    EventBus.getInstance().publish(ViewType.LOG_IN);;
                }
        );
    }

    @Override
    public void attachView(MainView view) {
        this.view = view; 
        super.attachView(view);
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }
}
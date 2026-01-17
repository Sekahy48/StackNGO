package mvc.controller;

import command.screen.ChangeScreenCommand;
import dataTransportLayer.EventBuffer;
import javafx.scene.control.Button;
import mvc.view.MainView;
import mvc.view.ViewType;

/**
 *
 * Controller that manages the logic related to {@link MainView}
 *
 */
public class MainViewController extends AbstractController<MainView> {

    public MainViewController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handleButton() {

        Button enterButton = this.view.getEnterButton();

        enterButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.LOG_IN));
                }
        );
    }

    @Override
    public void attachView(MainView view) {
        this.view = view; 
        super.attachView(view);
    }
}
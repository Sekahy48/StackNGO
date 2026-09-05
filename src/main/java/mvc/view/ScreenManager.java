package mvc.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import mvc.context.ViewContext;

public class ScreenManager implements IScreenManager {

    private final StackPane root;
    private final ViewContext viewContext;
    private Scene scene;

    public ScreenManager() {
        this.root = new StackPane();
        this.viewContext = new ViewContext();
    }

    public Parent getRoot() {
        return this.root;
    }

    public <T extends AbstractView> T getView(ViewType view) {
        return this.viewContext.getView(view);
    }

    @Override
    public void show(ViewType view) {
        Parent nextView = getView(view).getRoot();

        if (root.getChildren().isEmpty()) {
            root.getChildren().add(nextView);
        } else {
            root.getChildren().set(0, nextView);
        }
 
    }

    public void setScene(Scene scene) {
    this.scene = scene;

    scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
    }
}

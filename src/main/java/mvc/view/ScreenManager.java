package mvc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import mvc.context.ViewContext;

public class ScreenManager implements IScreenManager {

    private final StackPane root;
    private final ViewContext viewContext;
    private Scene scene;

    // Logo siempre visible
    private final ImageView logoOverlay;

    public ScreenManager() {
        this.root = new StackPane();
        this.viewContext = new ViewContext();

        // Crear el logo overlay
        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        logoOverlay = new ImageView(logo);
        logoOverlay.setFitWidth(105);
        logoOverlay.setFitHeight(105);
        logoOverlay.setPreserveRatio(true);
        StackPane.setAlignment(logoOverlay, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(logoOverlay, new Insets(10));
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

        // Determinar si queremos mostrar el logo en esta vista
        boolean showLogo = view != ViewType.LOG_IN && view != ViewType.SIGN_UP && view != ViewType.MAIN;

        // Reemplaza la vista en root
        if (root.getChildren().isEmpty()) {
            root.getChildren().add(nextView);
            if (showLogo) root.getChildren().add(logoOverlay);
        } else {
            // Reemplazar la primera capa (la vista anterior)
            root.getChildren().set(0, nextView);

            if (showLogo) {
                if (!root.getChildren().contains(logoOverlay)) root.getChildren().add(logoOverlay);
            } else {
                root.getChildren().remove(logoOverlay);
            }
        }
 
    }

    public void setScene(Scene scene) {
    this.scene = scene;

    scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
    }
}

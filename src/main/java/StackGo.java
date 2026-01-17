import creational.view.AbstractViewFactory;
import creational.view.ViewFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mvc.context.RuntimeContext;
import mvc.controller.CoreController;
import mvc.controller.ViewContext;
import mvc.view.ScreenManager;
import mvc.view.ViewType;

public class StackGo extends Application {

    @Override
    public void start(Stage stage) throws Exception { 

        AbstractViewFactory factory = new ViewFactory();
        ViewContext viewContext = new ViewContext(factory);
        ScreenManager screenManager = new ScreenManager(viewContext);
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setScreenManager(screenManager);
        CoreController coreController = new CoreController(runtimeContext);

        Scene scene = new Scene(screenManager.getRoot(), 800, 600);
        screenManager.setScene(scene);

        

        // Icono de la app
        Image appIcon = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        stage.getIcons().add(appIcon);

        // Mostrar la primera vista sin logo en MAIN
        screenManager.show(ViewType.MAIN);

        stage.setScene(scene);
        stage.show();

    }

}

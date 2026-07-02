import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage; 
import mvc.controller.CoreController; 
import mvc.view.ScreenManager;
import mvc.view.ViewType;

public class StackGo extends Application {

    @Override
    public void start(Stage stage) throws Exception { 

        ScreenManager screenManager = new ScreenManager(); 
        CoreController coreController = new CoreController(screenManager); 

        //RuntimeContext runtimeContext = new RuntimeContext(dataContext, sessionContext, systemContext);
        coreController.initServices();
        coreController.initUtilities();
        coreController.initControllers();
        /* runtimeContext.setSessionContext(sessionContext);
        runtimeContext.setSystemContext(systemContext);
        runtimeContext.setDataContext(dataContext); */
        //coreController.setContext(runtimeContext);

 

        Scene scene = new Scene(screenManager.getRoot(), 800, 600);
        screenManager.setScene(scene);
        
        

        // Icono de la app
        Image appIcon = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        stage.getIcons().add(appIcon);

        // Mostrar la primera vista sin logo en MAIN
        screenManager.show(ViewType.MAIN);

        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);

        

    }

}

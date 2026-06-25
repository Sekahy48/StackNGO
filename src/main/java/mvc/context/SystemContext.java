package mvc.context;

import mvc.controller.AbstractController;
import mvc.controller.CoreController;
import mvc.view.AbstractView;
import mvc.view.ScreenManager;
import mvc.view.ViewType;

public class SystemContext implements Context{
    private final ScreenManager screenManager;
    private final CoreController coreController;

    public SystemContext(ScreenManager screenManager, CoreController coreController){
        this.screenManager = screenManager;
        this.coreController = coreController;
    } 

    public <T extends AbstractView> T getView(ViewType view){
        return this.screenManager.getView(view);
    }

    public <T extends AbstractView> AbstractController<T> getController(ViewType controller){
        return this.coreController.getController(controller);
    }

    public ScreenManager getScreenManager(){
        return this.screenManager;
    }

}

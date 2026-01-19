package mvc.context;

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

    public AbstractView getView(ViewType view){
        return this.screenManager.getView(view);
    }
}

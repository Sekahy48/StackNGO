package service;

import mvc.context.SystemContext;
import mvc.view.ViewType;

public class SystemService implements IService {
    
    private SystemContext context;
    
    @Override
    public ServiceType getType() {
        return ServiceType.SYSTEM;
    }

    public void show(ViewType view){
        this.context.getController(view).updateAtShow();
        this.context.getScreenManager().show(view);
    }

    

}

package mvc.controller.user;

import dataTransportLayer.EventBuffer;
import mvc.controller.AbstractController;
import mvc.view.user.AbstractUserView;

public abstract class AbstractUserController<T extends AbstractUserView> extends AbstractController<T> {

    public AbstractUserController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(T view){
        super.attachView(view);
    }

    public abstract void handleButton();
}

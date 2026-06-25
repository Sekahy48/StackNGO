package mvc.controller.user;
 
import mvc.controller.AbstractController;
import mvc.view.user.AbstractUserView;

public abstract class AbstractUserController<T extends AbstractUserView> extends AbstractController<T> {

    @Override
    public void attachView(T view){
        super.attachView(view);
    }

    public abstract void handleButtons();
}

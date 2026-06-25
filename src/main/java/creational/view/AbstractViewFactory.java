package creational.view;

import event.EventBus;
import javafx.scene.Parent;
import mvc.view.AbstractView;
import mvc.view.ViewType;

public interface AbstractViewFactory {
    public abstract AbstractView create(ViewType view);
}

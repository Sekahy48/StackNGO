package mvc.controller;

import creational.view.AbstractViewFactory;
import mvc.view.AbstractView;
import mvc.view.ViewType;

import java.util.HashMap;
import java.util.Map;

public class ViewContext {

    private final AbstractViewFactory viewFactory;
    private final Map<ViewType, AbstractView> viewMap;

    public ViewContext(AbstractViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.viewMap = new HashMap<ViewType, AbstractView>();
    }

    public <T extends AbstractView> T getView(ViewType viewType) {
        AbstractView view = viewMap.get(viewType);

        if (view == null) {
            view = viewFactory.create(viewType);
            viewMap.put(viewType, view);
        }

        return (T) view;
    }
}

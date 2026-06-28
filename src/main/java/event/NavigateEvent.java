package event; 

import mvc.view.ViewType;

public record NavigateEvent(ViewType type) implements AppEvent{  
}

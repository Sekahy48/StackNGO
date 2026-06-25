package event;

import dataTransportLayer.GenericDTO;
import mvc.view.ViewType;

public record NavigateEvent(ViewType type) {  
}

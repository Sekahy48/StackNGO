package event;

import java.util.function.Consumer;

public class Subscription<T> {
    final Class<T> type;
    final Consumer<T> listener;
    
    Subscription(Class<T> type, Consumer<T> listener) {
        this.type = type;
        this.listener = listener;
    }
    
    void invoke(Object event) {
        if (type.isInstance(event))
            listener.accept(type.cast(event));
    }
}

package event; 

import java.util.*;
import java.util.function.Consumer;


public class EventBus {
    private static EventBus instance;
    private final Map<Class<?>, List<Subscription<?>>> listeners = new HashMap<>();

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public <T extends AppEvent> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>())
                 .add(new Subscription<>(type, listener));
    }

    public <T extends AppEvent> void unsubscribe(Class<T> type, Consumer<T> listener) {
        listeners.getOrDefault(type, List.of()).remove(new Subscription<>(type, listener));
    }

    public <T extends AppEvent> void publish(T event) {
        List<Subscription<?>> subs = listeners.get(event.getClass());
        if (subs != null) subs.forEach(s -> s.invoke(event));
    }
}
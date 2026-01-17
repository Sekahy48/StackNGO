package observer;

import java.util.ArrayList;
import java.util.List;

public class AbstractSubject<T> implements ISubject<T> {
    protected List<IObserver<T>> observers;

    public AbstractSubject() {
        observers = new ArrayList<>();
    }

    @Override
    public void attachObserver(IObserver<T> o) {
        this.observers.add(o);
    }

    @Override
    public void detachObserver(IObserver<T> o) {
        this.observers.remove(o);
    }

    public void notifyObservers(T value) {
        for (IObserver<T> observer : observers) {
            observer.update(value);
        }
    }

}

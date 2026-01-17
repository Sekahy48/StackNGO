package observer;

public interface ISubject<T> {
    void attachObserver(IObserver<T> o);
    void detachObserver(IObserver<T> o);
    void notifyObservers(T value);
}

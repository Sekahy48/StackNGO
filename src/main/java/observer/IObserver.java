package observer;

public interface IObserver<T> {
    void update(T buffer);
}

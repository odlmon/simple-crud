package sample.adapter;

public interface ControlAdapter<T> {

    T getValue();

    void setValue(T value);

    boolean isEmpty();

    void clear();

    Object getObject();
}

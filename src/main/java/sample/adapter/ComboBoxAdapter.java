package sample.adapter;

import javafx.scene.control.ComboBox;

public class ComboBoxAdapter<T> implements ControlAdapter<T> {

    ComboBox<T> comboBox;

    public ComboBoxAdapter(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public T getValue() {
        return comboBox.getValue();
    }

    @Override
    public void setValue(T value) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public ComboBox<T> getObject() {
        return null;
    }
}

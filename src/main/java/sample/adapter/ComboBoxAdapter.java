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
        comboBox.setValue(value);
    }

    @Override
    public boolean isEmpty() {
        return comboBox.getSelectionModel().isSelected(-1);
    }

    @Override
    public void clear() {
        comboBox.getSelectionModel().select(-1);
    }

    @Override
    public ComboBox<T> getObject() {
        return comboBox;
    }
}

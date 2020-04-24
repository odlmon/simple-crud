package sample.adapter;

import javafx.scene.control.TextField;

public class TextFieldAdapter implements ControlAdapter<String> {

    TextField textField;

    public TextFieldAdapter(TextField textField) {
        this.textField = textField;
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }

    @Override
    public boolean isEmpty() {
        return textField.getText().isEmpty();
    }

    @Override
    public void clear() {
        textField.setText("");
    }

    @Override
    public TextField getObject() {
        return textField;
    }
}

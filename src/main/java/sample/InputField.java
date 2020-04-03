package sample;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class InputField {

    private Object inputField;

    public InputField(Object inputField) {
        this.inputField = inputField;
    }

    public String getValue() {
        if (inputField instanceof TextField) {
            return ((TextField) inputField).getText();
        } else if (inputField instanceof ComboBox) {
            return (String) ((ComboBox) inputField).getValue();
        } else {
            return null;
        }
    }

    public void setValue(String value) {
       if (inputField instanceof TextField) {
           ((TextField) inputField).setText(value);
       } else if (inputField instanceof ComboBox) {
           ((ComboBox) inputField).setValue(value);
       }
    }

    public boolean isEmpty() {
        if ((inputField instanceof TextField) && ((TextField) inputField).getText().isEmpty()) {
            return true;
        } else {
            return (inputField instanceof ComboBox) && ((ComboBox) inputField).getSelectionModel().isSelected(-1);
        }
    }

    public void clear() {
        if (inputField instanceof TextField) {
            ((TextField) inputField).setText("");
        } else if (inputField instanceof ComboBox) {
            ((ComboBox) inputField).getSelectionModel().select(-1);
        }
    }

    public Object getObject() {
        return inputField;
    }
}

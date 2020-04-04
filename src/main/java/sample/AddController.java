package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.adapter.ComboBoxAdapter;
import sample.adapter.TextFieldAdapter;
import sample.annotation.Title;
import sample.adapter.ControlAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AddController {

    @FXML
    private ComboBox<String> cbClasses;

    @FXML
    private GridPane grid;

    @FXML
    private Button bConfirm;

    private List<Label> labelList = new ArrayList<>();

    private List<ControlAdapter<String>> inputFields = new ArrayList<>();

    private void handleComboBoxSelection() {
        try {
            bConfirm.setDisable(false);
            Class selectedClass = Class.forName(cbClasses.getValue());
            Field[] fields = ClassParser.getAllFields(selectedClass);
            labelList.clear();
            inputFields.clear();
            grid.getChildren().clear();
            for (int i = 0; i < fields.length; i++) {
                labelList.add(new Label(fields[i].getAnnotation(Title.class).value()));
                inputFields.add(resolveFieldType(fields[i]));
                grid.add(labelList.get(i), 0, i);
                grid.add((Node) inputFields.get(i).getObject(), 1, i);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Nonexistent class selected " + e);
        }
    }

    private void setFieldValue(Object object, ControlAdapter<String> inputField, Field field, Class fieldType) {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            if (int.class.equals(fieldType)) {
                inputField.setValue(Integer.toString((int) fieldValue));
            } else if (String.class.equals(fieldType)) {
                inputField.setValue((String) fieldValue);
            } else if (fieldType.isEnum()) {
                inputField.setValue(fieldValue.toString());
            } else if (!fieldType.isPrimitive()) {
                if ((fieldValue == null)) {
                    inputField.setValue("null");
                } else {
                    inputField.setValue(Integer.toString(fieldValue.hashCode()));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setFieldsValues(Object object) {
        Field[] fields = ClassParser.getAllFields(object.getClass());
        Class[] classes = ClassParser.getAllTypesOfFields(object.getClass());
        for (int i = 0; i < fields.length; i++) {
            setFieldValue(object, inputFields.get(i), fields[i], classes[i]);
        }
    }

    private void setUpdatingMode() {
        cbClasses.setDisable(true);
        cbClasses.setValue(Controller.updatingValue.getClass().getName());
        handleComboBoxSelection();
        setFieldsValues(Controller.updatingValue);
    }

    @FXML
    public void initialize() {
        ObservableList<String> data = FXCollections.observableArrayList(
                ClassParser.getAllClassesInPackage("sample"));
        cbClasses.setItems(data);
        bConfirm.setDisable(true);
        if (Controller.isUpdating) {
            setUpdatingMode();
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private ControlAdapter<String> resolveFieldType(Field field) {
        if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
            return new TextFieldAdapter(new TextField());
        } else if (field.getType().isEnum()) {
            return new ComboBoxAdapter<>(new ComboBox<>(FXCollections.observableArrayList(
                    ClassParser.getAllEnumValues(field.getType())
            )));
        } else {
            return new ComboBoxAdapter<>(new ComboBox<>(FXCollections.observableArrayList(
                    Stream.concat(Stream.of("null"), Controller.controller.instances
                            .stream()
                            .filter(item -> item.getClass().equals(field.getType()))
                            .map(Object::hashCode)
                            .map(Object::toString))
                    .toArray(String[]::new))));
        }
    }

    @FXML
    void onClassChoose(ActionEvent event) {
        handleComboBoxSelection();
    }

    private boolean inputFieldsIsEmpty() {
        return inputFields.stream().anyMatch(ControlAdapter::isEmpty);
    }

    private void clearFields() {
        inputFields.forEach(ControlAdapter::clear);
    }

    private Object parseField(ControlAdapter<String> inputField, Class fieldType) throws NumberFormatException{
        String value = inputField.getValue();
        if (int.class.equals(fieldType)) {
            return Integer.parseInt(value);
        } else if (String.class.equals(fieldType)) {
            return value;
        } else if (fieldType.isEnum()) {
            return Enum.valueOf(fieldType, value);
        } else if (!fieldType.isPrimitive()) {
            return (value.equals("null")) ? null :
                    Controller.controller.instances
                        .stream()
                        .filter(item -> item.hashCode() == Integer.parseInt(value))
                        .findFirst().get();
        }
        return null;
    }

    private Object[] getParamsFromFields(Class c, Class[] classes, List<ControlAdapter<String>> inputFields) {
        Object[] params = new Object[ClassParser.getFieldsCount(c)];
        for (int i = 0; i < inputFields.size(); i++) {
            try {
                params[i] = parseField(inputFields.get(i), classes[i]);
            } catch (NumberFormatException e) {
                inputFields.get(i).setValue("");
                showAlert(Alert.AlertType.ERROR, "Type Mismatch Error",
                        "Some fields have invalid values");
                return null;
            }
        }
        return params;
    }

    private void updateFields(Object object, Object[] params, Field[] fields) {
        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                fields[i].set(object, params[i]);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onConfirmPressAction(ActionEvent event) {
        if (inputFieldsIsEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Confirmation Error", "All fields must be filled");
        } else {
            try {
                Class selectedClass = Class.forName(cbClasses.getValue());
                Class[] classes = ClassParser.getAllTypesOfFields(selectedClass);
                Object[] params = getParamsFromFields(selectedClass, classes, inputFields);
                if (params != null) {
                    if (Controller.isUpdating) {
                        updateFields(Controller.updatingValue, params, ClassParser.getAllFields(selectedClass));
                        Controller.controller.updateTable(null);
                    } else {
                        Object instance = ClassParser.getFullConstructor(selectedClass).newInstance(params);
                        Controller.controller.updateTable(instance);
                        clearFields();
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}

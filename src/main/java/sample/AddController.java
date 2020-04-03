package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.annotation.Title;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AddController {

    @FXML
    private ComboBox<String> cbClasses;

    @FXML
    private GridPane grid;

    @FXML
    private Button bConfirm;
    /*TODO: для полей-объектов (не считая String) сделать возможность ставить null либо объект, но
       в случае List придумать что-то вроде ComboBox с множественным выбором сущностей-наследников Ammunition*/
    private List<Label> labelList = new ArrayList<>();

    private List<Object> inputFields = new ArrayList<>();

    @FXML
    public void initialize() {
        ObservableList<String> data = FXCollections.observableArrayList(
                ClassParser.getAllClassesInPackage("sample"));
        cbClasses.setItems(data);
        bConfirm.setDisable(true);
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private Object resolveFieldType(Field field) {
        if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
            return new TextField();
        } else if (field.getType().isEnum()) {
            return new ComboBox<>(FXCollections.observableArrayList(
                    ClassParser.getAllEnumValues(field.getType())
            ));
        } else {
            return new ComboBox<>(FXCollections.observableArrayList(
                    Stream.concat(Stream.of("null"), Controller.controller.instances
                            .stream()
                            .filter(item -> item.getClass().equals(field.getType()))
                            .map(Object::hashCode)
                            .map(Object::toString))
                    .toArray(String[]::new)));
        }
    }

    @FXML
    void onClassChoose(ActionEvent event) throws ClassNotFoundException {
        bConfirm.setDisable(false);
        Class selectedClass = Class.forName(cbClasses.getValue());
        System.out.println(ClassParser.getFullConstructor(selectedClass));
        Field[] fields = ClassParser.getAllFields(selectedClass);
        labelList.clear();
        inputFields.clear();
        grid.getChildren().clear();
        for (int i = 0; i < fields.length; i++) {
            labelList.add(new Label(fields[i].getAnnotation(Title.class).value()));
            inputFields.add(resolveFieldType(fields[i]));
            grid.add(labelList.get(i), 0, i);
            grid.add((Node) inputFields.get(i), 1, i);
        }
    }

    private boolean inputFieldsIsEmpty() {
        for (Object inputField : inputFields) {
            if (inputField instanceof TextField) {
                if (((TextField) inputField).getText().isEmpty()) {
                    return true;
                }
            } else if (inputField instanceof ComboBox) {
                if (((ComboBox) inputField).getSelectionModel().isSelected(-1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearFields() {
        for (Object inputField : inputFields) {
            if (inputField instanceof TextField) {
                ((TextField) inputField).setText("");
            } else if (inputField instanceof ComboBox) {
                ((ComboBox) inputField).getSelectionModel().select(-1);
            }
        }
    }

    private Object parseField(Class c, Object inputField, Class fieldType) throws NumberFormatException{
        if (inputField instanceof TextField) {
            if (fieldType.equals(int.class)) {
                return Integer.parseInt(((TextField) inputField).getText());
            } else if (fieldType.equals(String.class)) {
                return ((TextField) inputField).getText();
            }
        } else if (inputField instanceof ComboBox) {
            String value = (String) ((ComboBox) inputField).getValue();
            if (fieldType.isEnum()) {
                return Enum.valueOf(fieldType, value);
            } else if (!fieldType.isPrimitive()) {
                if (value.equals("null")) {
                    return null;
                } else {
                    return Controller.controller.instances
                            .stream()
                            .filter(item -> item.hashCode() == Integer.parseInt(value))
                            .findFirst().get();
                }
            }
        }
        return null;
    }

    private Object[] getParamsFromFields(Class c, Class[] classes, List<Object> inputFields) {
        Object[] params = new Object[ClassParser.getFieldsCount(c)];
        for (int i = 0; i < inputFields.size(); i++) {
            try {
                params[i] = parseField(c, inputFields.get(i), classes[i]);
            } catch (NumberFormatException e) {
                ((TextField) inputFields.get(i)).setText("");
                showAlert(Alert.AlertType.ERROR, "Type Mismatch Error",
                        "Some fields have invalid values");
                return null;
            }
        }
        return params;
    }
    //TODO: сделать разделения добавление и обновления данных, возможно через передачу флага
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
                    Object instance = ClassParser.getFullConstructor(selectedClass).newInstance(params);
                    Controller.controller.updateTable(instance);
                }
                clearFields();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}

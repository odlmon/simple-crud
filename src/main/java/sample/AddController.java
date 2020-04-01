package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AddController {

    @FXML
    private ComboBox<String> cbClasses;

    @FXML
    private GridPane grid;

    @FXML
    private Button bConfirm;

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
        }
        if (field.getType().isEnum()) {
            return new ComboBox<String>(FXCollections.observableArrayList(
                    ClassParser.getAllEnumValues(field.getType())
            ));
        }
        return new TextField();
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
                if (((String) ((ComboBox) inputField).getValue()).isEmpty()) {
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

    private Object[] getParamsFromFields(Class c, Class[] classes, List<Object> inputFields) {
        Object[] params = new Object[ClassParser.getFieldsCount(c)];
        for (int i = 0; i < inputFields.size(); i++) {
            try {
                params[i] = ClassParser.parseField(c, inputFields.get(i), classes[i]);
            } catch (NumberFormatException e) {
                ((TextField) inputFields.get(i)).setText("");
                showAlert(Alert.AlertType.ERROR, "Type Mismatch Error",
                        "Some fields have invalid values");
                return null;
            }
        }
        return params;
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

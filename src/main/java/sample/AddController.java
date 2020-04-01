package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AddController {

    @FXML
    private ComboBox<String> cbClasses;

    @FXML
    private GridPane grid;

    private List<Label> labelList = new ArrayList<>();

    private List<Object> inputFields = new ArrayList<>();

    @FXML
    public void initialize() {
        ObservableList<String> data = FXCollections.observableArrayList(
                ClassParser.getAllClassesInPackage("sample"));
        cbClasses.setItems(data);
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
}

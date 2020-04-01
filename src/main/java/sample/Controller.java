package sample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.attribute.HelmetType;
import sample.attribute.Size;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static sample.ClassParser.getAllClassesInPackage;
import static sample.ClassParser.getFullConstructor;

public class Controller {

    @FXML
    private MenuItem miAddNewInstance;

    public static Controller controller;

    @FXML
    public TableView<Object> table;

    public List<Object> instances = new ArrayList<>();

    @FXML
    public void initialize() throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String[] classes = getAllClassesInPackage("sample");
        for (String classItem: classes) {
            System.out.println(classItem);
        }
        Constructor fullConstructor = getFullConstructor(Class.forName(classes[1]));
        System.out.println(fullConstructor.newInstance(101, Size.S, "model1", 2020, HelmetType.Open, 3));
        TableColumn<Object, Integer> hashColumn = new TableColumn<>("Hash");
        hashColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().hashCode()));
        TableColumn<Object, Class> classColumn = new TableColumn("Class");
        classColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getClass()));
        table.getColumns().clear();
        table.getColumns().addAll(hashColumn, classColumn);
//        table.getItems().add(fullConstructor.newInstance(101, Size.S, "model1", 2020, HelmetType.Open, 3));
    }

    @FXML
    void addNewInstanceAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("add.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add new instance");
            stage.setScene(new Scene(root, 381, 400));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error to create new stage " + e);
        }
    }

    public void updateTable(Object instance) {
        instances.add(instance);
        table.getItems().add(instance);
    }

}

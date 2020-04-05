package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.serialize.BinarySerializer;
import sample.serialize.JsonSerializer;
import sample.serialize.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Controller {

    public static boolean isUpdating;

    public static Object updatingValue;

    public static Controller controller;

    @FXML
    public TableView<Object> table;

    public List<Object> instances = new ArrayList<>();

    private <T> TableColumn<Object, T> createColumn(String name, Function<Object, T> func, double relativeWidth,
                                                    boolean isResizable) {
        TableColumn<Object, T> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(func.apply(data.getValue())));
        column.prefWidthProperty().bind(table.widthProperty().multiply(relativeWidth));
        column.setResizable(isResizable);
        return column;
    }

    private void createNewModalStage(String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("add.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root, 381, 400));
            stage.show();
        } catch (IOException e) {
            isUpdating = false;
            System.err.println("Error to create new stage " + e);
        }
    }

    private void initializeTable() {
        table.getColumns().clear();
        table.getColumns().addAll(
                createColumn("Hash", Object::hashCode, 0.1, false),
                createColumn("Class", Object::getClass, 0.2, false),
                createColumn("Value", Object::toString, 0.697, false));
        table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            updatingValue = newValue;
            isUpdating = true;
            createNewModalStage("Update instance");
        }));
    }

    @FXML
    public void initialize() {
        initializeTable();
    }

    @FXML
    void addNewInstanceAction(ActionEvent event) {
        isUpdating = false;
        createNewModalStage("Add new instance");
    }

    private Serializer getSerializerByExtension(String extension) {
        if (extension.equals("bin")) {
            return new BinarySerializer();
        } else if (extension.equals("json")) {
            return new JsonSerializer();
        } else {
            return null;
        }
    }

    private void serializeInstances(Serializer serializer, File file) {
        instances.forEach(instance -> serializer.serialize(instance, file));
    }

    @FXML
    void onSaveAsAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Binary files (*.bin)", "*.bin"),
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"),
                new FileChooser.ExtensionFilter("STR files (*.str)", "*.str")
        );
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            Serializer serializer = getSerializerByExtension(fileChooser
                    .getSelectedExtensionFilter()
                    .getExtensions()
                    .get(0)
                    .substring(2));
            serializeInstances(serializer, file);
        }
    }

    public void updateTable(Object instance) {
        if (instance != null) {
            instances.add(instance);
            table.getItems().add(instance);
        } else {
            table.refresh();
        }
    }

}

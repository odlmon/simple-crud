package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.serialize.BinarySerializer;
import sample.serialize.JsonSerializer;
import sample.serialize.Serializer;
import sample.serialize.YamlSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static sample.ClassParser.getAllFields;
import static sample.ClassParser.getAllTypesOfFields;

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

    private String getClassName(Object object) {
        String[] str = object.getClass().getName().split("\\.");
        return str[str.length - 1];
    }

    private void changeAllObjectsWithNested(Object object) {
        instances.stream().
                filter(item -> Arrays.stream(getAllTypesOfFields(item.getClass()))
                        .anyMatch(type -> type.equals(object.getClass()))).
                forEach(item -> Arrays.stream(getAllFields(item.getClass()))
                        .forEach(field -> {
                            field.setAccessible(true);
                            try {
                                if (object.equals(field.get(item))) {
                                    field.set(item, null);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }));
    }

    private void initializeTable() {
        table.getColumns().clear();
        table.getColumns().addAll(
                createColumn("Hash", Object::hashCode, 0.1, false),
                createColumn("Class", this::getClassName, 0.15, false),
                createColumn("Value", Object::toString, 0.747, false));
        var cm = new ContextMenu();
        var miUpdate = new MenuItem("Update");
        miUpdate.setOnAction((ActionEvent event) -> {
            updatingValue = table.getSelectionModel().getSelectedItem();
            isUpdating = true;
            createNewModalStage("Update instance");
        });
        var miDelete = new MenuItem("Delete");
        miDelete.setOnAction((ActionEvent event) -> {
            Object object = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(object);
            instances.remove(object);
            changeAllObjectsWithNested(object);
            table.refresh();
        });
        cm.getItems().addAll(miUpdate, miDelete);
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                cm.show(table, t.getScreenX(), t.getScreenY());
            }
        });
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
        return switch(extension) {
            case "bin" -> new BinarySerializer();
            case "json" -> new JsonSerializer();
            case "yaml" -> new YamlSerializer();
            default -> null;
        };
    }

    private void serializeInstances(Serializer serializer, File file) {
        serializer.serialize(instances.toArray(), file);
    }

    private ArrayList<Object> deserializeInstances(Serializer serializer, File file) {
        Object[] objects = serializer.deserialize(file);
        return (objects != null) ? new ArrayList<>(Arrays.asList(objects)) : new ArrayList<>();
    }

    private Pair<Serializer, File> getSerializerFromChooser(boolean isSaveMode) {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Binary files (*.bin)", "*.bin"),
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"),
                new FileChooser.ExtensionFilter("YAML files (*.yaml)", "*.yaml")
        );
        File file = isSaveMode ? fileChooser.showSaveDialog(new Stage()) :
                fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            return new Pair<>(getSerializerByExtension(fileChooser
                    .getSelectedExtensionFilter()
                    .getExtensions()
                    .get(0)
                    .substring(2)), file);
        } else {
            return null;
        }
    }

    @FXML
    void onOpenAction(ActionEvent event) {
        Pair<Serializer, File> pair = getSerializerFromChooser(false);
        if (pair != null) {
            instances = deserializeInstances(pair.getKey(), pair.getValue());
            if (instances.isEmpty() || instances.contains(null)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error while deserialization");
                alert.setContentText("File empty or invalid");
                alert.show();
            } else {
                table.getItems().clear();
                instances.forEach(i -> table.getItems().add(i));
            }
        }
    }

    @FXML
    void onSaveAsAction(ActionEvent event) {
        Pair<Serializer, File> pair = getSerializerFromChooser(true);
        if (pair != null) {
            serializeInstances(pair.getKey(), pair.getValue());
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

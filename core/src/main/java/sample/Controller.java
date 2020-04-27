package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import sample.serialize.BinarySerializer;
import sample.serialize.JsonSerializer;
import sample.serialize.Serializer;
import sample.serialize.YamlSerializer;
import service.ExtensionData;
import service.ObjectCodec;

import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static sample.ClassParser.getAllFields;
import static sample.ClassParser.getAllTypesOfFields;

public class Controller {

    public static boolean isUpdating;

    public static Object updatingValue;

    public static Controller controller;

    @FXML
    public TableView<Object> table;

    @FXML
    private Menu setCodec;

    public List<Object> instances = new ArrayList<>();

    private Map<ObjectCodec, RadioMenuItem> currentServices = new HashMap<>();

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
                createColumn("Value", Object::toString, 0.743, false));
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

    private static List<ObjectCodec> loadServices(ModuleLayer layer) {
        return ServiceLoader
                .load(layer, ObjectCodec.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }

    private List<ObjectCodec> getServices(String path) {
        Path pluginsDir = Paths.get(path);
        ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);
        List<String> plugins = pluginsFinder
                .findAll()
                .stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .collect(Collectors.toList());
        Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);
        ModuleLayer layer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());
        return loadServices(layer);
    }

    @FXML
    void onSelect(Event event) {
        List<ObjectCodec> codecs = getServices("core/plugins");
        if (currentServices.isEmpty()) {
            codecs.forEach(codec -> currentServices.put(codec, new RadioMenuItem(codec.getClass().getName())));
            setCodec.getItems().addAll(currentServices.values());
        } else {
            ArrayList<RadioMenuItem> actualItems = currentServices.entrySet().stream()
                    .filter(itemEntry -> codecs.stream()
                            .map(codec -> codec.getClass().getName())
                            .collect(Collectors.toList())
                            .contains(itemEntry.getKey().getClass().getName()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toCollection(ArrayList::new));
            currentServices = currentServices.entrySet().stream()
                    .filter(itemEntry -> actualItems.contains(itemEntry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            setCodec.getItems().retainAll(currentServices.values());
        }
        var toggleGroup = new ToggleGroup();
        currentServices.values().forEach(item -> item.setToggleGroup(toggleGroup));
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

    private Optional<ObjectCodec> getSelectedCodec() {
        return currentServices.entrySet().stream()
                .filter(item -> item.getValue().isSelected())
                .findFirst()
                .map(Map.Entry::getKey);
    }

    private void serializeInstances(Serializer serializer, File file) {
        serializer.serialize(instances.toArray(), file);
    }

    private ArrayList<Object> deserializeInstances(Serializer serializer, File file) {
        Object[] objects = serializer.deserialize(file);
        return (objects != null) ? new ArrayList<>(Arrays.asList(objects)) : new ArrayList<>();
    }

    private boolean hasEncodingExtension(FileChooser fileChooser) {
        return currentServices.keySet().stream()
                .map(codec -> codec.getExtensionData().format())
                .collect(Collectors.toList())
                .contains(fileChooser.getSelectedExtensionFilter().getExtensions().get(0));
    }

    private Optional<ObjectCodec> getCodecByExtension(FileChooser fileChooser) {
        return currentServices.keySet().stream()
                .filter(codec -> codec.getExtensionData().format().equals(
                            fileChooser.getSelectedExtensionFilter().getExtensions().get(0)))
                .findFirst();
    }

    private void addExtensions(FileChooser fileChooser, boolean isSaveMode) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Binary files (*.bin)", "*.bin"),
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"),
                new FileChooser.ExtensionFilter("YAML files (*.yaml)", "*.yaml")
        );
        if (!isSaveMode) {
            fileChooser.getExtensionFilters().addAll(
                    currentServices.keySet().stream()
                            .map(codec -> {
                                ExtensionData data = codec.getExtensionData();
                                return new FileChooser.ExtensionFilter(data.description(), data.format());
                            })
                            .collect(Collectors.toCollection(ArrayList::new))
            );
        }
    }

    private File updateFileWithCodec(File file, Optional<ObjectCodec> objectCodec) {
        String path = null;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File finalFile = file;
        objectCodec.ifPresent(codec -> codec.decode(finalFile));
        File newFile = new File(path.substring(0, path.lastIndexOf(".")));
        file.renameTo(newFile);
        return newFile;
    }

    private IOBundle getSerializerFromChooser(boolean isSaveMode) {
        var fileChooser = new FileChooser();
        addExtensions(fileChooser, isSaveMode);
        File file = isSaveMode ? fileChooser.showSaveDialog(new Stage()) :
                fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            if (!isSaveMode && hasEncodingExtension(fileChooser)) {
                file = updateFileWithCodec(file, getCodecByExtension(fileChooser));
            }
            String path = null;
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isSaveMode) {
                return new IOBundle(getSerializerByExtension(
                        path.substring(path.lastIndexOf(".") + 1)),
                        getSelectedCodec(), file);
            } else {
                return new IOBundle(getSerializerByExtension(
                        path.substring(path.lastIndexOf(".") + 1)),
                        getCodecByExtension(fileChooser), file);
            }
        } else {
            return null;
        }
    }

    @FXML
    void onOpenAction(ActionEvent event) {
        IOBundle ioBundle = getSerializerFromChooser(false);
        if (ioBundle != null) {
            instances = deserializeInstances(ioBundle.serializer(), ioBundle.file());
            ioBundle.objectCodec().ifPresent(codec -> codec.encode(ioBundle.file()));
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
        IOBundle ioBundle = getSerializerFromChooser(true);
        if (ioBundle != null) {
            serializeInstances(ioBundle.serializer(), ioBundle.file());
            ioBundle.objectCodec().ifPresent(codec -> codec.encode(ioBundle.file()));
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

package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.reflections.Reflections;
import sample.attribute.HelmetType;
import sample.attribute.Size;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

import static sample.ClassParser.getAllClassesInPackage;
import static sample.ClassParser.getFullConstructor;

public class Controller {

    @FXML
    private Label label;

    @FXML
    private MenuItem miAddNewInstance;

    @FXML
    public void initialize() throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String[] classes = getAllClassesInPackage("sample");
        for (String classItem: classes) {
            System.out.println(classItem);
        }
        Constructor fullConstructor = getFullConstructor(Class.forName(classes[1]));
        System.out.println(fullConstructor.newInstance(101, Size.S, "model1", 2020, HelmetType.Open, 3));
    }

    @FXML
    void addNewInstanceAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("add.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add new instance");
            stage.setScene(new Scene(root, 381, 400));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error to create new stage " + e);
        }
    }

}

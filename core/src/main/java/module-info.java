import service.ObjectCodec;

open module Test {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires reflections;
    requires com.google.gson;
    requires plugin;

    uses ObjectCodec;

    exports sample;
}
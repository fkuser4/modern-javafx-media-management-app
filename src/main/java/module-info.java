module com.mydigitalmedia.app.digitalmediaapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires org.slf4j;
    requires java.desktop;
    requires java.net.http;
    requires okhttp3;
    requires org.json;
    requires eu.hansolo.fx.charts;

    exports com.mydigitalmedia.mediaapp.ui;
    opens com.mydigitalmedia.mediaapp.ui to javafx.fxml;
    exports com.mydigitalmedia.mediaapp;
    opens com.mydigitalmedia.mediaapp to javafx.fxml;
    opens com.mydigitalmedia.mediaapp.model to javafx.base;
}
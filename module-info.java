module com.example.assignment1javafxchart {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.assignment1javafxchart to javafx.fxml;
    exports com.example.assignment1javafxchart;
}
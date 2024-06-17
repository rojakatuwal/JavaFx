package com.example.assignment1javafxchart;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector extends Application {

    private Stage stage;
    private Scene emissionScene, pieChartScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Load the application icon
        Image icon = new Image(getClass().getResourceAsStream("/com/example/assignment1javafxchart/download.jpg"));
        stage.getIcons().add(icon);


        // Connect to MySQL database
        String url = "jdbc:mysql://localhost:3306/NepalEmissionsDB";
        String user = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url, user, password);

        // Set up TableView for emission data
        TableView<CO2Data> tableView = createTableView(conn);

        // Set up PieChart for the data
        PieChart pieChart = createPieChart(conn);

        // This  button will switch to PieChart scene
        Button switchToPieChartButton = new Button("Switch to Pie Chart");
        switchToPieChartButton.setOnAction(e -> stage.setScene(pieChartScene));

        //You can switch back to TableView scene by clicking this button
        Button switchToTableViewButton = new Button("Back to Table View");
        switchToTableViewButton.setOnAction(e -> stage.setScene(emissionScene));

        // Layout for TableView scene
        VBox emissionLayout = new VBox(10);
        emissionLayout.getChildren().addAll(tableView, switchToPieChartButton);
        emissionScene = new Scene(emissionLayout, 800, 600);

        // Layout for PieChart scene
        VBox pieChartLayout = new VBox(10);
        pieChartLayout.getChildren().addAll(pieChart, switchToTableViewButton);
        pieChartScene = new Scene(pieChartLayout, 800, 600);

        // Close database connection
        conn.close();

        // Set initial scene
        stage.setScene(emissionScene);
        stage.setTitle("Nepal CO2 Emissions Data");
        stage.show();
    }

    private TableView<CO2Data> createTableView(Connection conn) throws Exception {
        TableView<CO2Data> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<CO2Data, Integer> yearColumn = new TableColumn<>("Year");
        TableColumn<CO2Data, Double> emissionsColumn = new TableColumn<>("CO2 Emissions per Capita");

        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        emissionsColumn.setCellValueFactory(new PropertyValueFactory<>("co2Emissions"));

        tableView.getColumns().addAll(yearColumn, emissionsColumn);

        // Query data from database
        String query = "SELECT * FROM Nepal_CO2_Emissions";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Populate table with data
        ObservableList<CO2Data> dataList = FXCollections.observableArrayList();
        while (resultSet.next()) {
            int year = resultSet.getInt("Year");
            double emissions = resultSet.getDouble("CO2_Emissions_per_Capita");
            CO2Data data = new CO2Data(year, emissions);
            dataList.add(data);
        }

        tableView.setItems(dataList);

        // Close database resources
        resultSet.close();
        stmt.close();

        return tableView;
    }

    private PieChart createPieChart(Connection conn) throws Exception {
        // Query data from database
        String query = "SELECT * FROM Nepal_CO2_Emissions";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Prepare data for pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        while (resultSet.next()) {
            int year = resultSet.getInt("Year");
            double emissions = resultSet.getDouble("CO2_Emissions_per_Capita");
            pieChartData.add(new PieChart.Data(String.valueOf(year), emissions));
        }

        // Close database resources
        resultSet.close();
        stmt.close();

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Nepal CO2 Emissions Per Capita in metric tone");

        return pieChart;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // CO2Data class for TableView
    public static class CO2Data {
        private final int year;
        private final double co2Emissions;

        public CO2Data(int year, double co2Emissions) {
            this.year = year;
            this.co2Emissions = co2Emissions;
        }

        public int getYear() {
            return year;
        }

        public double getCo2Emissions() {
            return co2Emissions;
        }
    }
}

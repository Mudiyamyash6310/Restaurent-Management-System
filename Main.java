package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.Connection;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Connection conn = DBUtil.connect();
        if (conn != null) {
            System.out.println("✅ Connected to Oracle XE!");
        } else {
            System.out.println("❌ Connection failed.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
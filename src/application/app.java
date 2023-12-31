package application;

import java.io.IOException;
import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class app extends Application {

public Connection connection;
    @Override
    public void init() throws Exception {
        db db=new db();
        connection =db.getConnection();
                
    }

    @Override
    public void start(Stage loginStage) throws IOException {
        
        loginStage.setTitle("Login");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        DashboardController dashboardController = loader.getController();
        dashboardController.connection=connection;
        loginStage.show();

    }
    //show login ui
    @Override
    public void stop() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

}
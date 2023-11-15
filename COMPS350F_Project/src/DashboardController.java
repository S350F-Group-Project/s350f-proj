import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DashboardController {
@FXML
private TextField un;
@FXML
private TextField pw;
@FXML
private Button loginbutton;
@FXML
private Button logoutbutton;
@FXML
private Button canceltbutton;
@FXML
private Button AcademicRecordsbutton;
@FXML
private Button mycoursebutton;
@FXML
private Button ManageAccountsbutton;
@FXML
GridPane coursePane;


public String role;
Connection connection;
public student loggedInStudent = new student();
public admin loggedInAdmin = new admin();;
public teacher loggedInTeacher = new teacher();;


@FXML
private void initialize() throws Exception {
    db db = new db();
    connection=db.getConnection();
}
    


@FXML
private void handleLogoutButton() throws IOException {
        // Load the login scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        // Get the stage of the current scene
        Stage currentStage = (Stage) logoutbutton.getScene().getWindow();
        // Set the new scene on the stage
        currentStage.setScene(scene);
        currentStage.show();
       
        System.out.println("Logging out Student object: " + "Username= " + loggedInStudent.getUsername() + " Password= " + loggedInStudent.getPassword() + " Name= " + loggedInStudent.getName() + " Role= " + loggedInStudent.getRole());

        loggedInStudent = null;
        loggedInTeacher = null;
        loggedInAdmin = null;
}


@FXML
private void handleCancelButton() throws IOException {
    Stage loginStage = (Stage) loginbutton.getScene().getWindow();
    loginStage.close();
}

@FXML
private void handleAcademicRecordsButton(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        student loggedInStudent = (student) stage.getUserData();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AcademicRecords.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
    
        stage.setScene(scene);
        DashboardController dashboardController=loader.getController();
        //dashboardController.loggedInStudent=loggedInStudent;        
        stage.show();
        System.out.println("Switch to AcademicRecords");
}
@FXML
private void handleManageAccountButton() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageAccount.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
    
        // Get the stage of the current scene
        Stage currentStage = (Stage) logoutbutton.getScene().getWindow();
        // Set the new scene on the stage
        currentStage.setScene(scene);
        DashboardController dashboardController=loader.getController();
        
        
        currentStage.show();
        System.out.println("Switch to ManageAccount");
}

@FXML
private void handleLoginButton(ActionEvent event) throws IOException {
    String username = un.getText();
    String password = pw.getText();

    try {
       // Check if the username and password are valid
       PreparedStatement statement = connection.prepareStatement("SELECT role, username, password,name FROM students WHERE username = ? AND password = ? UNION SELECT role, username, password,name FROM teachers WHERE username = ? AND password = ? UNION SELECT role, username, password,name FROM admins WHERE username = ? AND password = ?");
       statement.setString(1, username);
       statement.setString(2, password);
       statement.setString(3, username);
       statement.setString(4, password);
       statement.setString(5, username);
       statement.setString(6, password);
       ResultSet resultSet = statement.executeQuery();  
    
     
    if (resultSet.next()) {
    role = resultSet.getString("role");
    String name = resultSet.getString("name");
    if ( null != role){ // Username and password are correct
        System.out.println("Role= "+role);
        System.out.println("Name= "+name);
        Stage loginStage = (Stage) loginbutton.getScene().getWindow();
        loginStage.close();
        
        Node node = (Node) event.getSource();
  // Step 3
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
            // Load the dashboard view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
         
        Parent root = loader.load();
        coursePane = (GridPane) loader.getNamespace().get("coursePane");
        // Create a new stage for the dashboard
        
        stage.setTitle("Dashboard");
        stage.setScene(new Scene(root));
        DashboardController dashboardController=loader.getController();
       
        
        stage.show();
           switch (role) {
           // Handle student login
               case "student":
                   loggedInStudent.setAll(username, password, name, role);
                   
                    stage.setUserData(loggedInStudent);
                   
                    System.out.println("Student object: " + "Username= " + loggedInStudent.getUsername() + " Password= " + loggedInStudent.getPassword() + " Name= " + loggedInStudent.getName() + " Role= " + loggedInStudent.getRole());
                   loggedInStudent.myCourse(username, password, connection, coursePane, stage,loggedInStudent);
                   break;
           // Handle teacher login
               case "teacher":
                   
                   break;
           // Handle admin login
               case "admin":
                   
                   break;
               default:
                   break;
           }
    }
} else {
    // Username and password are not correct in students, teachers, and admin tables
    showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password.");
}
    resultSet.close();
    statement.close();
} catch (SQLException e) {
    e.printStackTrace();
    showAlert(AlertType.ERROR, "Database Error", "Failed to query the database.");
}
}
@FXML
private void handleMyCourseButton(ActionEvent event) throws IOException, SQLException, Exception {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                student loggedInStudent = (student) stage.getUserData();
                
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();
                coursePane = (GridPane) loader.getNamespace().get("coursePane");
                Scene scene = new Scene(root);
                // Get the stage of the current scene
               
                DashboardController dashboardController=loader.getController();
               // dashboardController.loggedInStudent=loggedInStudent;
               
                //pass object
               
                // Set the new scene on the stage
                stage.setScene(scene);
                stage.show();

       loggedInStudent.myCourse(loggedInStudent.getUsername(), loggedInStudent.getPassword(), connection, coursePane, stage,loggedInStudent);
       System.out.println("back to my course with Student object: " + "Username= " + loggedInStudent.getUsername() + " Password= " + loggedInStudent.getPassword() + " Name= " + loggedInStudent.getName() + " Role= " + loggedInStudent.getRole());

}

private void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}
}
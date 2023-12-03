package application;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
private Button UpdatePersonalInfotButton;
@FXML
GridPane coursePane;
@FXML
private ImageView photoImageView;
@FXML
private Label nameLab;
@FXML
private Label usernameLabel;
@FXML
private TextField phoneNumTF;
@FXML
private TextField emailTF;
@FXML
private TextField passwordTF;
@FXML
private TextField confirmPasswordTF;

@FXML
private Button attachPhotoButton;
@FXML
private Button updateButton;
public String role;
Connection connection;
public user loggedInUser = new user();


@FXML
private void initialize() throws Exception {
    db db = new db();
    connection=db.getConnection();

}
@FXML
private void handleLoginButton(ActionEvent event) throws IOException {
    String username = un.getText();
    String password = pw.getText();

    try {
       // Check if the username and password are valid
       PreparedStatement statement = connection.prepareStatement("SELECT role, username, password,name FROM users WHERE username = ? AND password = ?");
       statement.setString(1, username);
       statement.setString(2, password);
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
  
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
          
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
         
        Parent root = loader.load();
        coursePane = (GridPane) loader.getNamespace().get("coursePane");
        
        stage.setTitle("Dashboard");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        DashboardController dashboardController=loader.getController();
       	loggedInUser.setAll(username, password, name, role); 
        stage.setUserData(loggedInUser);
        System.out.println("Student object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());
        
        stage.show();
           switch (role) {
           // Handle student login
               case "student":
                    loggedInUser.studentCourseDashboard(username, password, connection, coursePane, stage,loggedInUser);
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
private void handleLogoutButton() throws IOException {
        // Load the login scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        // Get the stage of the current scene
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        
        // Set the new scene on the stage
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
       
        System.out.println("Logging out Student object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());

        loggedInUser = null;
}

@FXML
private void handleCancelButton() throws IOException {
    Stage loginStage = (Stage) loginbutton.getScene().getWindow();
    loginStage.close();
}

@FXML
private void handleAcademicRecordsButton(ActionEvent event) throws IOException, SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user loggedInUser = (user) stage.getUserData();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AcademicRecords.fxml"));
        Parent root = loader.load();
        TableView<AcademicRecord> recordTableView = (TableView<AcademicRecord>) root.lookup("#recordTableView");
        TableColumn<AcademicRecord, String> termCol = null;
        TableColumn<AcademicRecord, String> courseIDCol = null;
        TableColumn<AcademicRecord, String> subjectCol = null;
        TableColumn<AcademicRecord, String> statusCol = null;
        TableColumn<AcademicRecord, String> unitCol = null;
        TableColumn<AcademicRecord, String> gradeCol = null;
        TableColumn<AcademicRecord, String> topStudentCol = null;
        ObservableList<TableColumn<AcademicRecord, ?>> columns = recordTableView.getColumns();
        for (TableColumn<AcademicRecord, ?> column : columns) {
            if (column.getId().equals("termCol")) {
            	termCol = (TableColumn<AcademicRecord, String>) column;
            } else if (column.getId().equals("courseIDCol")) {
            	courseIDCol = (TableColumn<AcademicRecord, String>) column;
            } else if (column.getId().equals("subjectCol")) {
            	subjectCol = (TableColumn<AcademicRecord, String>) column;
            }else if (column.getId().equals("statusCol")) {
            	statusCol = (TableColumn<AcademicRecord, String>) column;
            } else if (column.getId().equals("unitCol")) {
            	unitCol = (TableColumn<AcademicRecord, String>) column;
            }else if (column.getId().equals("gradeCol")) {
            	gradeCol = (TableColumn<AcademicRecord, String>) column;
            } else if (column.getId().equals("topStudentCol")) {
            	topStudentCol = (TableColumn<AcademicRecord, String>) column;
            }
        }
        PreparedStatement statement = connection.prepareStatement("SELECT COURSEID,TERM,GRADE,SUBJECT,STATUS,UNIT,TOPSTUDENT FROM AcademicRecords where username=?");
        statement.setString(1, loggedInUser.username);
        ResultSet resultSet=statement.executeQuery();
        List<AcademicRecord> records = new ArrayList<>();
        while (resultSet.next()) {
            String courseID = resultSet.getString("COURSEID");
            String term = resultSet.getString("TERM");
            String grade = resultSet.getString("GRADE");
            String subject = resultSet.getString("SUBJECT");
            String status = resultSet.getString("STATUS");
            String unit = resultSet.getString("UNIT");
            String topStudent = resultSet.getString("TOPSTUDENT");
     
            records.add(new AcademicRecord(term, subject,courseID,status,unit,grade,topStudent));
            }
        termCol.setCellValueFactory(new PropertyValueFactory<>("term"));
        courseIDCol.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        topStudentCol.setCellValueFactory(new PropertyValueFactory<>("topStudent"));
        
        ObservableList<AcademicRecord> data = FXCollections.observableArrayList(records);
        recordTableView.setItems(data);
        
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        
        
        DashboardController dashboardController=loader.getController();
      
        PreparedStatement statement2 = connection.prepareStatement("SELECT CGPA FROM users where username=?");
        statement2.setString(1, loggedInUser.username);
        ResultSet resultSet2 = statement2.executeQuery();
        if(resultSet2.next()) {
        	Label cgpa = (Label) root.lookup("#cgpa");
        	cgpa.setText(resultSet2.getString(1));
        }
     
        
        stage.show();
        System.out.println("Switch to AcademicRecords");
}
@FXML
private void handleUpdatePersonalInfotButton(ActionEvent event) throws IOException, SQLException {
	 	Node node = (Node) event.getSource();
	 	Stage stage = (Stage) node.getScene().getWindow();
	 	user loggedInUser = (user) stage.getUserData();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updatePersonalInfo.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
    
        // Get the stage of the current scene
        Stage currentStage = (Stage) logoutbutton.getScene().getWindow();
        // Set the new scene on the stage
        currentStage.setScene(scene);
        DashboardController dashboardController=loader.getController();
        
        PreparedStatement ps = connection.prepareStatement("Select NAME,IMAGE,EMAIL,PHONENUMBER,USERNAME,PASSWORD FROM users where username=?");
        
        ps.setString(1, loggedInUser.username);
        
        ResultSet rs =ps.executeQuery();
        rs.next();
        	String name=rs.getString(1);
        	String email=rs.getString(3);
        	String phoneNumber=rs.getString(4);
        	String username=rs.getString(5);
        	String password=rs.getString(6);
        	Blob image=rs.getBlob(2);
   
  
        	dashboardController.nameLab.setText(name);
        	
        	if(email!=null) {
        		dashboardController.emailTF.setText(email);
        	}
        	if(phoneNumber!=null) {
        		dashboardController.phoneNumTF.setText(phoneNumber);
        	}
        	dashboardController.usernameLabel.setText(username);
        	
        	if (image != null) {
        	    try (InputStream inputStream = image.getBinaryStream()) {
        	        Image img = new Image(inputStream);
        	        dashboardController.photoImageView.setImage(img);
        	    } catch (IOException e) {
        	        e.printStackTrace();
        	    }
        	}
        currentStage.setResizable(false);
        currentStage.show();
        System.out.println("Switch to UpdatePersonalInfo");
}

@FXML
private void handleAttachPhotoButton(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Photo");
    // Set the file extension filters if necessary
     fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.png"));
    
    // Show the file chooser dialog
    File selectedFile = fileChooser.showOpenDialog(null);
    
    if (selectedFile != null) {
        // Create an Image object from the selected file
        Image image = new Image(selectedFile.toURI().toString());
        
        // Set the Image as the content of the ImageView
       
        this.photoImageView.setImage(image);
        showAlert(AlertType.INFORMATION, "Success", "Attached photo.");
    }
}

@FXML
private void handleUpdateButton(ActionEvent event) throws SQLException {
    // Retrieve the modified values from the UI components
	String username=this.usernameLabel.getText();
    String newPhoneNumber = this.phoneNumTF.getText();
    if (newPhoneNumber.length() != 8) {
        showAlert(AlertType.INFORMATION, "Error", "Phone Number must have exactly 8 characters");
        return; }
    
    String newEmail = this.emailTF.getText();
    if (!newEmail.contains("@")) {
        showAlert(AlertType.INFORMATION, "Error", "Invalid Email format");
        return;
    }
   
    String confirmPassword=this.confirmPasswordTF.getText();
    String newPassword=this.passwordTF.getText();
    System.out.println(confirmPassword+newPassword);
    if(!(newPassword.equals(confirmPassword))) {
    	showAlert(AlertType.INFORMATION, "Error", "New Password not equal to Confirm Password ");
    	return;
    	
    }
    
    PreparedStatement ps=connection.prepareStatement("Select password from users where username=?");
    ps.setString(1,username);
    ResultSet rs= ps.executeQuery();
    rs.next();
    String oldPassword= rs.getString(1);
    if(newPassword.equals(oldPassword)){
    	showAlert(AlertType.INFORMATION, "Error", "New password can not be equal to your old passwprd ");
    	return;
    }
    // Get the image from the ImageView
    Image newImage = this.photoImageView.getImage();
    // Convert the Image to a byte array
    byte[] imageBytes = null;
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(newImage, null);
        ImageIO.write(bufferedImage, "png", outputStream);
        imageBytes = outputStream.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    // Update the database records with the modified values
    PreparedStatement ps2 = connection.prepareStatement("UPDATE users SET  IMAGE=?, EMAIL=?, PHONENUMBER=?, PASSWORD=? where username=?");
   
    ps2.setBytes(1, imageBytes);
    ps2.setString(2, newEmail);
    ps2.setString(3, newPhoneNumber);
    ps2.setString(4, newPassword);
    ps2.setString(5, username);
    ps2.executeUpdate();
    
    // Show a success message or perform any other necessary actions
    
    showAlert(AlertType.INFORMATION, "Success", "Updated Personal Info successfully.");
    Node node = (Node) event.getSource();
    Stage stage = (Stage) node.getScene().getWindow();
    user loggedInUser = (user) stage.getUserData();
    loggedInUser.setPassword(newPassword);
    stage.setUserData(loggedInUser);
    
}


@FXML
private void handleMyCourseButton(ActionEvent event) throws IOException, SQLException, Exception {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                user loggedInUser = (user) stage.getUserData();
                
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();
                coursePane = (GridPane) loader.getNamespace().get("coursePane");
                Scene scene = new Scene(root);
                // Get the stage of the current scene
                
                DashboardController dashboardController=loader.getController();
                
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

                loggedInUser.studentCourseDashboard(loggedInUser.getUsername(), loggedInUser.getPassword(), connection, coursePane, stage,loggedInUser);
       System.out.println("back to my course with Student object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());

}

private void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}
}

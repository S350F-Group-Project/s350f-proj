package application;


import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;

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
public GridPane coursePane;
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
private Button backButton;
@FXML
private Button manageAccountsButton;
@FXML
private Button attachPhotoButton;
@FXML
private Button backupButton;
@FXML
private Button updateButton;
public String role;
Connection connection;
public user loggedInUser = new user();
@FXML
private TableColumn<userModel, String> idCol;
@FXML
private TableColumn<userModel, String> nameCol;
@FXML
private TableColumn<userModel, String> unCol;
@FXML
private TableColumn<userModel, String> pwCol;
@FXML
private TableColumn<userModel, String> roleCol;

@FXML
TableView<userModel> userTableView;

@FXML
private void initialize() throws Exception {
    db db = new db();
    connection=db.getConnection();

}


private ObservableList<userModel> getUserList() throws SQLException {
    ObservableList<userModel> userList = FXCollections.observableArrayList();

       PreparedStatement ps = connection.prepareStatement("SELECT id, name, username, password, role FROM users");
       ResultSet rs = ps.executeQuery();

       while (rs.next()) {
           String id = rs.getString("id");
           String n = rs.getString("name");
           String un = rs.getString("username");
           String pw = rs.getString("password");
           String role = rs.getString("role");

           // Create a User object with the retrieved information
           userModel user = new userModel(id, n, un, pw, role);
           System.out.println(user);
           // Add the User object to the TableView
        userList.add(user);
       }
    return userList;
}
public void setupUserTableView() throws SQLException {
    // Set up cell value factories for each column
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    unCol.setCellValueFactory(new PropertyValueFactory<>("username"));
    pwCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    pwCol.setCellFactory(TextFieldTableCell.forTableColumn());
    pwCol.setOnEditCommit(event -> {
        userModel user = event.getRowValue();
        String newPassword = event.getNewValue();
        boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
        boolean hasLowercase = !newPassword.equals(newPassword.toUpperCase());
        boolean hasMinimumLength = newPassword.length() >= 10;
        if (!(hasUppercase && hasLowercase && hasMinimumLength)) {
            showAlert(AlertType.INFORMATION, "Error", "Password must contain at least one uppercase letter, one lowercase letter, and have a minimum length of 10 characters");
            return;
        }
        user.setPassword(newPassword);
        PreparedStatement statement;
		try {
			statement = connection.prepareStatement("UPDATE users set password = ? where username=?");
			statement.setString(1, user.getPassword());
			statement.setString(2, user.getUsername());
	
			ResultSet resultSet = statement.executeQuery();  
			 showAlert(AlertType.INFORMATION, "Success", "password updated successfully");
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
   
    });
    roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
    roleCol.setCellFactory(TextFieldTableCell.forTableColumn());
    roleCol.setOnEditCommit(event -> {
        userModel user = event.getRowValue();
        String newRole = event.getNewValue();
        
        if(!(newRole.equalsIgnoreCase("student") || newRole.equalsIgnoreCase("teacher") || newRole.equalsIgnoreCase("admin"))) {
        	showAlert(AlertType.INFORMATION, "Error", "Valid roles are 'student', 'teacher', and 'admin'");
            return;
        }
        user.setRole(newRole);
        PreparedStatement statement;
		try {
			statement = connection.prepareStatement("UPDATE users set role = ? where username=?");
			statement.setString(1, user.getRole());
			statement.setString(2, user.getUsername());
	
			ResultSet resultSet = statement.executeQuery();  
			 showAlert(AlertType.INFORMATION, "Success", "role updated successfully");
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
        
        
    });

    // Enable editing for the table
    userTableView.setEditable(true);


    userTableView.setItems(getUserList());
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
          
           switch (role) {
           // Handle student login
               case "student":
            	   FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                   Parent studentRoot = studentLoader.load();
                   stage.setTitle("Student Dashboard");
                   stage.setScene(new Scene(studentRoot));
                   stage.setResizable(false);
                   DashboardController studentController = studentLoader.getController();
                   loggedInUser.setAll(username, password, name, role);
                   stage.setUserData(loggedInUser);
                   loggedInUser.studentCourseDashboard(username, password, connection, studentController.coursePane, stage, loggedInUser);
                   System.out.println("Student object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());

                   stage.show();
                   break;
                   
           // Handle teacher login
               case "teacher":
            	   FXMLLoader teacherLoader = new FXMLLoader(getClass().getResource("teacherDashboard.fxml"));
                   Parent teacherRoot = teacherLoader.load();
                   stage.setTitle("Teacher Dashboard");
                   stage.setScene(new Scene(teacherRoot));
                   stage.setResizable(false);
                   DashboardController teacherController = teacherLoader.getController();
                   loggedInUser.setAll(username, password, name, role);
                   stage.setUserData(loggedInUser);
                   loggedInUser.teacherCourseDashboard(username, password, connection, teacherController.coursePane, stage, loggedInUser);
                   System.out.println("Teacher object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());

                   stage.show();
            	   
                   break;
                   
           // Handle admin login
               case "admin":
            	   FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("adminDashboard.fxml"));
            	    Parent adminRoot = adminLoader.load();
            	    stage.setTitle("Admin Dashboard");
            	    stage.setScene(new Scene(adminRoot));
            	    stage.setResizable(false);
            	    DashboardController adminController = adminLoader.getController();
            	    loggedInUser.setAll(username, password, name, role);
            	    stage.setUserData(loggedInUser);
            	    // Set up the TableView columns and data
            	    adminController.setupUserTableView();

            	    System.out.println("Admin object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());
            	    stage.show();
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
private void handleManageAccountsButton(ActionEvent event) throws SQLException, IOException {
	    FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("adminDashboard.fxml"));
	 	Node node = (Node) event.getSource();
	 
	 	Stage stage = (Stage) node.getScene().getWindow();
	    Parent adminRoot = adminLoader.load();
	    stage.setTitle("Admin Dashboard");
	    stage.setScene(new Scene(adminRoot));
	    stage.setResizable(false);
	    DashboardController adminController = adminLoader.getController();
	    user loggedInUser = (user) stage.getUserData();
	
	    // Set up the TableView columns and data
	    adminController.setupUserTableView();

	    System.out.println("Admin object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());
	    stage.show();
}

@FXML
private void handleBackupButton() {
    List<String> tableNames = new ArrayList<>();
    tableNames.add("ACADEMICRECORDS");
    tableNames.add("ASSIGNMENTS");
    tableNames.add("COURSES");
    tableNames.add("PROGRAMS");
    tableNames.add("SUBMITTEDASSIGNMENTS");
    tableNames.add("TEACHINGMATERIALS");
    tableNames.add("USERS");

    try {
        // Choose the directory to save the backup file
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            // Create the backup file
            String backupFileName = "backup.sql";
            File backupFile = new File(selectedDirectory.getPath(), backupFileName);

            // Check if the backup file already exists
            int counter = 1;
            while (backupFile.exists()) {
                backupFileName = "backup(" + counter + ").sql";
                backupFile = new File(selectedDirectory.getPath(), backupFileName);
                counter++;
            }

            FileWriter writer = new FileWriter(backupFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            // Export each table as SQL statements
            for (String tableName : tableNames) {
                String query = "SELECT * FROM " + tableName;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Write SQL statements to the backup file
                while (resultSet.next()) {
                    StringBuilder insertStatement = new StringBuilder();
                    insertStatement.append("INSERT INTO ");
                    insertStatement.append(tableName);
                    insertStatement.append(" VALUES (");

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        Object value = resultSet.getObject(i);
                        insertStatement.append(value);

                        if (i != columnCount) {
                            insertStatement.append(", ");
                        }
                    }

                    insertStatement.append(");");
                    bufferedWriter.write(insertStatement.toString());
                    bufferedWriter.newLine();
                }

                resultSet.close();
                statement.close();
            }

            bufferedWriter.close();
            writer.close();

            System.out.println("Backup created successfully.");
            showAlert(AlertType.INFORMATION, "Success", "Backup successful.");
        }
    } catch (IOException | SQLException e) {
        e.printStackTrace();
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
        
        
        if(loggedInUser.role.equalsIgnoreCase("student")) {
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
        }else if(loggedInUser.role.equalsIgnoreCase("teacher")) {
        	
        	 FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherAcademicRecords.fxml"));
             Parent root = loader.load();
             Scene scene = new Scene(root);
             DashboardController teacherController=loader.getController();
             
             loggedInUser.teacherAcademicRecord(loggedInUser.username, loggedInUser.password, connection, teacherController.coursePane, stage, loggedInUser);
             stage.setResizable(false);
             stage.setScene(scene);
             stage.show();
        }
}
@FXML
private void handleUpdatePersonalInfotButton(ActionEvent event) throws IOException, SQLException {
	Node node = (Node) event.getSource();
 	Stage stage = (Stage) node.getScene().getWindow();
 	user loggedInUser = (user) stage.getUserData();
	if(loggedInUser.role.equalsIgnoreCase("student")||loggedInUser.role.equalsIgnoreCase("teacher")) {
	 	
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
	}else if(loggedInUser.role.equalsIgnoreCase("admin")) {
		
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminUpdatePersonalInfo.fxml"));
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
}

@FXML
private void handleAttachPhotoButton(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Photo");

     fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.png"));
    

    File selectedFile = fileChooser.showOpenDialog(null);
    
    if (selectedFile != null) {

        Image image = new Image(selectedFile.toURI().toString());
 
        this.photoImageView.setImage(image);
        showAlert(AlertType.INFORMATION, "Success", "Attached photo.");
    }
}
@FXML
private void handleUpdateButton(ActionEvent event) throws SQLException {

    String username = this.usernameLabel.getText();
    String newPhoneNumber = this.phoneNumTF.getText();
    if (newPhoneNumber.length() != 8) {
        showAlert(AlertType.INFORMATION, "Error", "Phone Number must have exactly 8 characters");
        return;
    }

    String newEmail = this.emailTF.getText();
    if (!newEmail.contains("@")) {
        showAlert(AlertType.INFORMATION, "Error", "Invalid Email format");
        return;
    }

    String confirmPassword = this.confirmPasswordTF.getText();
    String newPassword = this.passwordTF.getText();

    // Check if both new password and confirm password fields are empty
    boolean isPasswordEmpty = newPassword.isEmpty() && confirmPassword.isEmpty();

    // If any of the password fields are non-empty, perform validation checks
    if (!isPasswordEmpty) {
        // Check if new password matches confirm password
        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.INFORMATION, "Error", "New Password does not match Confirm Password");
            return;
        }

        // Check if new password is equal to the old password
        PreparedStatement ps = connection.prepareStatement("SELECT password FROM users WHERE username=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String oldPassword = rs.getString(1);
        if (newPassword.equals(oldPassword)) {
            showAlert(AlertType.INFORMATION, "Error", "New password cannot be equal to your old password");
            return;
        }
        boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
        boolean hasLowercase = !newPassword.equals(newPassword.toUpperCase());
        boolean hasMinimumLength = newPassword.length() >= 10;
        if (!(hasUppercase && hasLowercase && hasMinimumLength)) {
            showAlert(AlertType.INFORMATION, "Error", "Password must contain at least one uppercase letter, one lowercase letter, and have a minimum length of 10 characters");
            return;
        }
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

    String updateQuery = "UPDATE users SET IMAGE=?, EMAIL=?, PHONENUMBER=?";
    if (!isPasswordEmpty) {
        updateQuery += ", PASSWORD=?";
    }
    updateQuery += " WHERE username=?";
    PreparedStatement ps2 = connection.prepareStatement(updateQuery);
    ps2.setBytes(1, imageBytes);
    ps2.setString(2, newEmail);
    ps2.setString(3, newPhoneNumber);
    if (!isPasswordEmpty) {
        ps2.setString(4, newPassword);
        ps2.setString(5, username);
    } else {
        ps2.setString(4, username);
    }
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
           
                
                DashboardController dashboardController=loader.getController();
                
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                switch(loggedInUser.role) {
                case"student":
                	loggedInUser.studentCourseDashboard(loggedInUser.getUsername(), loggedInUser.getPassword(), connection, coursePane, stage,loggedInUser);
                    System.out.println("back to my course with Student object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());
                	break;
                case "teacher":
                	loggedInUser.teacherCourseDashboard(loggedInUser.getUsername(), loggedInUser.getPassword(), connection, coursePane, stage,loggedInUser);
                    System.out.println("Teacher object: " + "Username= " + loggedInUser.getUsername() + " Password= " + loggedInUser.getPassword() + " Name= " + loggedInUser.getName() + " Role= " + loggedInUser.getRole());

                    break;
                case"admin":
                    break;
                
                }
                

}

private void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}
}
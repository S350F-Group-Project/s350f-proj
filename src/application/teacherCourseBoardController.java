package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class teacherCourseBoardController {
    @FXML
    private Button backButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private ListView<HBox> assignmentListView;
    @FXML
    private GridPane coursePane;
    private String courseID;
    private Connection connection;
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

  
    public void setCoursePane(GridPane coursePane) {
        this.coursePane = coursePane;
    }

    
    void initialize() throws Exception {
        db db = new db();
        connection = db.getConnection();
        
        // Get the index of the currently selected tab
        int selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();

        // Get the currently opened week
        int openedWeek = -1;
        if (selectedTabIndex >= 0 && selectedTabIndex < tabPane.getTabs().size()) {
            Tab selectedTab = tabPane.getTabs().get(selectedTabIndex);
            String tabText = selectedTab.getText();
            openedWeek = Integer.parseInt(tabText.substring(tabText.lastIndexOf(" ") + 1));
        }
        
        // Clear the assignmentListView
        assignmentListView.getItems().clear();
        populateAssignmentListView(assignmentListView, connection);

        // Clear the TabPane by removing all tabs
        tabPane.getTabs().clear();

        // Set tab closing policy
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        // Get the total number of weeks from the course table
        int totalWeeks = 14;

        // Create tabs for each week
        for (int week = 1; week <= totalWeeks; week++) {
            Tab tab = new Tab("Week " + week);

            // Create a ListView to display filenames from the teachingmaterials table
            ListView<HBox> listView = new ListView<>();

            populateListView(listView, week, connection);

            // Populate the ListView with filenames for the specific week

            VBox vbox = new VBox(listView);
            tab.setContent(vbox);

            // Add the tab to the TabPane
            tabPane.getTabs().add(tab);
        }

        // Set the previously selected tab as the currently selected tab
        if (openedWeek != -1) {
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                Tab tab = tabPane.getTabs().get(i);
                String tabText = tab.getText();
                int week = Integer.parseInt(tabText.substring(tabText.lastIndexOf(" ") + 1));
                if (week == openedWeek) {
                    tabPane.getSelectionModel().select(i);
                    break;
                }
            }
        } else if (selectedTabIndex >= 0 && selectedTabIndex < tabPane.getTabs().size()) {
            // If no previously opened week, select the same index as before
            tabPane.getSelectionModel().select(selectedTabIndex);
        }
    }
    
@FXML
private void handleBackButton(ActionEvent event) throws SQLException, IOException{
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                user loggedInUser = (user) stage.getUserData();
  
                FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherDashboard.fxml"));
                Parent root = loader.load();
                coursePane = (GridPane) loader.getNamespace().get("coursePane");
                Scene scene = new Scene(root);
                DashboardController dashboardController=loader.getController();
     
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                dashboardController.connection=connection;
               
                loggedInUser.teacherCourseDashboard(loggedInUser.getUsername(), loggedInUser.getPassword(), connection, coursePane, stage,loggedInUser);
                System.out.println("Back to dashboard with Student object: " + "username= " + loggedInUser.username + " password= " + loggedInUser.password + " name= " + loggedInUser.name + " role= " + loggedInUser.role);    
}
    
private void populateListView(ListView<HBox> listView, int week,Connection connection) throws Exception {
        // Populate the ListView with filenames from the teachingmaterials table for the given week
        try {
      
            String sql = "SELECT filenumber,filename, files FROM teachingmaterials WHERE week = ? AND COURSEID=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, week);
            statement.setString(2, courseID);
    
            ResultSet resultSet = statement.executeQuery();

            // Iterate over the result set and create Hyperlinks for each record
            while (resultSet.next()) {
                String filename = resultSet.getString("filename");
                Blob blob = resultSet.getBlob("files");
                String fileNumber=resultSet.getString("filenumber");

                // Create a Hyperlink with the filename
                Hyperlink hyperlink = new Hyperlink(filename);
                // Create a Delete button for each teaching material
                Button deleteButton = new Button("X");
                deleteButton.setOnAction(event -> {
                    try {
                        deleteTeachingMaterial(filename,fileNumber);
                        initialize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // Set the click event handler for the Hyperlink
                hyperlink.setOnMouseClicked(event -> {
                    try {
                        // Download the blob file when the Hyperlink is clicked
                        downloadBlobFile(blob, filename);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // Add the Hyperlink to the ListView
                HBox hbox = new HBox(hyperlink, deleteButton);
                listView.getItems().add(hbox);
                
            }
           
            Button upload = new Button("upload");
            upload.setOnAction(event -> {
               try {
				handleUploadButton(week);
			} catch (Exception e) {
		
				e.printStackTrace();
			}
            });
            HBox hbox = new HBox(upload);
            listView.getItems().add(hbox);
            
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
private void deleteTeachingMaterial(String filename, String fileNumber) throws SQLException {

    String sql = "DELETE FROM teachingmaterials WHERE filename = ? AND courseID = ? AND filenumber=?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, filename);
    statement.setString(2, courseID);
    statement.setString(3, fileNumber);

    statement.executeUpdate();

    statement.close();
}
private void handleUploadButton(int week) throws Exception {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Teaching Material");
    File selectedFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());

    if (selectedFile != null) {
        try {
            // Read the selected file and upload it to the database
            byte[] fileData = Files.readAllBytes(selectedFile.toPath());
            uploadTeachingMaterial(week, selectedFile.getName(), fileData);

            // Refresh the ListView to display the newly uploaded material
            initialize();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
private void uploadTeachingMaterial(int week, String filename, byte[] fileData) throws Exception {
    
    String sql = "INSERT INTO teachingmaterials (week, filename, files, courseID) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, week);
    statement.setString(2, filename);
    statement.setBytes(3, fileData);
    statement.setString(4, courseID);

    statement.executeUpdate();

    statement.close();
}
private void populateAssignmentListView(ListView<HBox> listView, Connection connection) throws Exception {
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {

        String sql = "SELECT * FROM assignments WHERE courseID = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, courseID);
        resultSet = statement.executeQuery();
        
        while (resultSet.next()) {
        	String title = resultSet.getString("title");
            String filename = resultSet.getString("filename");
            Blob blob = resultSet.getBlob("files");
            String description = resultSet.getString("description");
            String fileNumber = resultSet.getString("filenumber");
            
            
            String deadline = resultSet.getString("deadline");
            java.sql.Date compareDeadline = resultSet.getDate("deadline");

            System.out.println(deadline);
            Hyperlink hyperlink = new Hyperlink(title);

            hyperlink.setOnAction(event -> {
            	 FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherAssignmentPopup.fxml"));
                 Parent root;
				try {
					byte[] fileData = null;
					if(blob!=null) {
					  fileData = blob.getBytes(1, (int) blob.length());
					}
				    String grade;
				   	String status;
					root = loader.load();
					  Node node = (Node) event.getSource();
			          Stage stage = (Stage) node.getScene().getWindow();
			          user loggedInUser = (user) stage.getUserData();
			          PreparedStatement ps2= connection.prepareStatement("SELECT STATUS, GRADE from submittedAssignments WHERE username=? and title=? ");
			          ps2.setString(1, loggedInUser.username);
			          ps2.setString(2, title);
			          ResultSet rs2 =ps2.executeQuery();
			          if(rs2.next()) {
			        	  status=rs2.getString(1);
			        	  grade= rs2.getString(2);
			          }
			          else {
			        	  status="Not Submitted";
			        	  grade=" Not Marked";
			          }
			          teacherAssignmentPopupController teacherAssignmentpopupController = loader.getController();
				      teacherAssignmentpopupController.setAssignmentDetails(title,description, status, deadline, filename, grade);
				      teacherAssignmentpopupController.setFileData(fileData);
				      teacherAssignmentpopupController.setCourseID(courseID);
				      teacherAssignmentpopupController.setFileNumber(fileNumber);
				      teacherAssignmentpopupController.setConnection(connection);
				      teacherAssignmentpopupController.setTitle(title);			    
		          
		              Scene scene = new Scene(root);
		              Stage popupStage = new Stage();
		              popupStage.setScene(scene);
		              popupStage.setResizable(false);
		              popupStage.show();
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
           
            });
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(event -> {
                // Show confirmation dialog for deletion
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Deletion");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Are you sure you want to delete this assignment?");
                
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        PreparedStatement p = connection.prepareStatement("DELETE FROM assignments WHERE courseID = ? AND filenumber=?");
                        p.setString(1, courseID);
                        p.setString(2, fileNumber);

                        p.executeUpdate();

                     
                        p.close();
                        initialize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }});
            HBox hbox = new HBox(hyperlink,deleteButton);
            listView.getItems().add(hbox);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
    	Button newA = new Button("New Assignment");
    	newA.setOnAction(event -> {
            try {
				handleNewAssignmentButton();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
    	try {
			initialize();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        });
        HBox hbox = new HBox(newA);
        listView.getItems().add(hbox);
    	
        // Close the statement and result set
        if (statement != null) {
            statement.close();
        }
        if (resultSet != null) {
            resultSet.close();
        }
    }
}
private void handleNewAssignmentButton() throws Exception {
	try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createNewAssignment.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Create New Assignment");
        popupStage.setScene(new Scene(root));

        // Show the popup window
        popupStage.showAndWait();

        // After the popup window is closed, you can handle the assignment creation logic here
        // Retrieve the input values from the UI elements in the "CreateNewAssignment.fxml" file
        CreateNewAssignmentController createNewAssignmentController = loader.getController();
        Boolean cancel=createNewAssignmentController.getCancel();
        if(cancel==true) {
         return;
        }
        String title = createNewAssignmentController.getTitle();
        String deadline = createNewAssignmentController.getDeadline();
        byte[] fileData = createNewAssignmentController.getFileData();
        String description = createNewAssignmentController.getDescription();
        String fileName=createNewAssignmentController.getFileName();
        // Call the method to create a new assignment using the retrieved values
        uploadAssignment(fileName, fileData,deadline, description,title);
    } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void uploadAssignment(String filename, byte[] fileData, String deadline, String description, String title) throws Exception {
    // Prepare the SQL statement to insert the teaching material into the database
    String sql = "INSERT INTO Assignments (filename, files, courseID, description, title, deadline) VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, filename);
    statement.setBytes(2, fileData);
    statement.setString(3, courseID);
    statement.setString(4, description);
    statement.setString(5, title);
    
    // Convert the deadline String to a java.sql.Date object
    if(!deadline.isBlank()) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    java.util.Date parsedDate = dateFormat.parse(deadline);
    java.sql.Date sqlDeadline = new java.sql.Date(parsedDate.getTime());
    statement.setDate(6, sqlDeadline);
    // Execute the SQL statement
    statement.executeUpdate();
    // Close the statement
    statement.close();
    }else {
    	statement.setDate(6, null);
    	statement.executeUpdate();
    	 // Close the statement
        statement.close();
    }
  
}
private void downloadBlobFile(Blob blob, String filename) throws IOException, SQLException, Exception {
    // Open a directory chooser dialog to select the download location
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select Download Location");
    Stage stage = new Stage();
    File selectedDirectory = directoryChooser.showDialog(stage);

    if (selectedDirectory != null) {
        // Create the complete file path
        String filePath = selectedDirectory.getAbsolutePath() + File.separator + filename;

        // Check if the file already exists in the directory
        File file = new File(filePath);
        int counter = 1;
        while (file.exists()) {
            // Append a counter to the filename
            String[] parts = filename.split("\\.");
            String baseName = parts[0];
            String extension = parts[1];
            String newFilename = baseName + "(" + counter + ")." + extension;
            filePath = selectedDirectory.getAbsolutePath() + File.separator + newFilename;
            file = new File(filePath);
            counter++;
        }

        // Retrieve the blob data
        byte[] data = blob.getBytes(1, (int) blob.length());

        // Create a ByteArrayInputStream from the blob data
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        // Create a FileOutputStream to write the blob data to the file
        FileOutputStream outputStream = new FileOutputStream(filePath);

        // Read the data from the input stream and write it to the output stream
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        // Close the streams
        inputStream.close();
        outputStream.close();

        System.out.println("File downloaded successfully to: " + filePath);
    }
}
  public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
}
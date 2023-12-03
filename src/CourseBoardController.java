package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class CourseBoardController {
    @FXML
    private Button backButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private ListView<Hyperlink> assignmentListView;
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
    	
        db db=new db();
        connection=db.getConnection();
        populateAssignmentListView(assignmentListView,connection);
        // Set tab closing policy
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        
        // Get the total number of weeks from the course table
        int totalWeeks = 14;

        // Create tabs for each week
        for (int week = 1; week <= totalWeeks; week++) {
            Tab tab = new Tab("Week " + week);

            // Create a ListView to display filenames from the teachingmaterials table
            ListView<Hyperlink> listView = new ListView<>();
            populateListView(listView, week,connection); // Populate the ListView with filenames for the specific week
         
            VBox vbox = new VBox(listView);
            tab.setContent(vbox);

            //Add the tab to the TabPane
            tabPane.getTabs().add(tab);
        }
        
         if(connection == null){
            System.out.println("null con");
        }
       
    }
    
@FXML
private void handleBackButton(ActionEvent event) throws SQLException, IOException{
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
                dashboardController.connection=connection;
               
                loggedInUser.studentCourseDashboard(loggedInUser.getUsername(), loggedInUser.getPassword(), connection, coursePane, stage,loggedInUser);
                System.out.println("Back to dashboard with Student object: " + "username= " + loggedInUser.username + " password= " + loggedInUser.password + " name= " + loggedInUser.name + " role= " + loggedInUser.role);    
}
    
private void populateListView(ListView<Hyperlink> listView, int week,Connection connection) throws Exception {
        // Populate the ListView with filenames from the teachingmaterials table for the given week
        try {
            // Prepare the SQL statement
            String sql = "SELECT filename, files FROM teachingmaterials WHERE week = ? AND COURSEID=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, week);
            statement.setString(2, courseID);
            // Execute the query and retrieve the result set
            ResultSet resultSet = statement.executeQuery();

            // Iterate over the result set and create Hyperlinks for each record
            while (resultSet.next()) {
                String filename = resultSet.getString("filename");
                Blob blob = resultSet.getBlob("files");

                // Create a Hyperlink with the filename
                Hyperlink hyperlink = new Hyperlink(filename);

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
                listView.getItems().add(hyperlink);
            }
            // Close the resources
            resultSet.close();
            statement.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

private void populateAssignmentListView(ListView<Hyperlink> listView, Connection connection) throws Exception {
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
        // Prepare the SQL statement to retrieve assignment details for the specific courseID
        String sql = "SELECT * FROM assignments WHERE courseID = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, courseID);
        resultSet = statement.executeQuery();
        
        while (resultSet.next()) {
        	String title = resultSet.getString("title");
            String filename = resultSet.getString("filename");
            Blob blob = resultSet.getBlob("files");
            String description = resultSet.getString("description");
            
            String deadline = resultSet.getString("deadline");
            java.sql.Date compareDeadline = resultSet.getDate("deadline");
            
            Hyperlink hyperlink = new Hyperlink(title);

            hyperlink.setOnAction(event -> {
            	 FXMLLoader loader = new FXMLLoader(getClass().getResource("AssignmentPopup.fxml"));
                 Parent root;
				try {
					byte[] fileData = null;
					if(blob!=null) {
					  fileData = blob.getBytes(1, (int) blob.length());
					}
				   	String status;
					root = loader.load();
					  Node node = (Node) event.getSource();
			          Stage stage = (Stage) node.getScene().getWindow();
			          user loggedInUser = (user) stage.getUserData();
			          PreparedStatement ps2= connection.prepareStatement("SELECT STATUS from submittedAssignments WHERE username=? and title=? ");
			          ps2.setString(1, loggedInUser.username);
			          ps2.setString(2, title);
			          ResultSet rs2 =ps2.executeQuery();
			          if(rs2.next()) {
			        	  status=rs2.getString(1);
			          }
			          else {
			        	  status="Not Submitted";
			          }
				      AssignmentPopupController AssignmentpopupController = loader.getController();
		              AssignmentpopupController.setAssignmentDetails(title,description, status, deadline, filename);
		              AssignmentpopupController.setFileData(fileData);
		              AssignmentpopupController.setStatus(status);
		              AssignmentpopupController.setCourseID(courseID);
		              AssignmentpopupController.setUsername(loggedInUser.username);
		              AssignmentpopupController.setConnection(connection);
		              AssignmentpopupController.setTitle(title);
		              AssignmentpopupController.setDeadlineDate(compareDeadline);
		          
		              
		              Scene scene = new Scene(root);
		              Stage popupStage = new Stage();
		              popupStage.setScene(scene);
		              popupStage.setResizable(false);
		              popupStage.show();
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
           
            });

            listView.getItems().add(hyperlink);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Close the statement and result set
        if (statement != null) {
            statement.close();
        }
        if (resultSet != null) {
            resultSet.close();
        }
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
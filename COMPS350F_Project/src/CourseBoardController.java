import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class CourseBoardController {
    @FXML
    private Button backButton;
    @FXML
    private TabPane tabPane;

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
        System.out.println(courseID+"in init");
       
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
                student loggedInStudent = (student) stage.getUserData();
                //stage.setUserData(this.loggedInStudent);
              
                // Load the previous scene (Dashboard.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();
                coursePane = (GridPane) loader.getNamespace().get("coursePane");
                Scene scene = new Scene(root);
                DashboardController dashboardController=loader.getController();
                //dashboardController.loggedInStudent=loggedInStudent;
                // Get the stage of the current scene
               
                // Set the new scene on the stage
                stage.setScene(scene);
                stage.show();
                dashboardController.connection=connection;
               
                loggedInStudent.myCourse(loggedInStudent.getUsername(), loggedInStudent.getPassword(), connection, coursePane, stage,loggedInStudent);
                System.out.println("Back to dashboard with Student object: " + "username= " + loggedInStudent.username + " password= " + loggedInStudent.password + " name= " + loggedInStudent.name + " role= " + loggedInStudent.role);
            
                 
                
       
      
}
    

private void populateListView(ListView<Hyperlink> listView, int week,Connection connection) throws Exception {
        // Populate the ListView with filenames from the teachingmaterials table for the given week
        try {
            // Prepare the SQL statement
            String sql = "SELECT filename, files FROM teachingmaterials WHERE week = ? AND COURSEID=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, week);
            System.out.println(courseID+ "in pop");
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
private void downloadBlobFile(Blob blob, String filename) throws IOException, SQLException, Exception {
    // Specify the directory where the file will be saved
    String directoryPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;

    // Create the complete file path
    String filePath = directoryPath + filename;

    // Check if a file with the same filename already exists
    File file = new File(filePath);
    int fileIndex = 1;
    while (file.exists()) {
        // Append a unique identifier to the filename
        String newFilename = getUniqueFilename(filename, fileIndex);
        filePath = directoryPath + newFilename;
        file = new File(filePath);
        fileIndex++;
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
}

private String getUniqueFilename(String filename, int index) {
    // Split the filename into name and extension
    int dotIndex = filename.lastIndexOf(".");
    String name = filename.substring(0, dotIndex);
    String extension = filename.substring(dotIndex);

    // Append the index to the filename and construct the unique filename
    return name + "_" + index + extension;
}
  public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
}
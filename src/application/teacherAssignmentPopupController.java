package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class teacherAssignmentPopupController {

	@FXML
    private Label titleLabel;
    @FXML
    private TextArea descriptionLabel;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Hyperlink downloadLink;
    @FXML
    private Button attachFileButton;
    @FXML
    private Button studentSubButton;

    private String filename;
    private byte[] fileData;
    private byte[] attachedFileData;
    public String courseID;
    public String fileNumber;
    private String title;
   
    private Connection connection;
    
    
    @FXML
    private void initialize() {
    
    }
    
    public void setAssignmentDetails(String title,String description, String status, String deadline, String filename, String grade) {
    	titleLabel.setText(title);
        descriptionLabel.setText(description);
        deadlineLabel.setText(deadline);
        downloadLink.setText(filename);
        this.filename = filename;
    }

    @FXML
    private void handleDownload() {
        if (fileData == null) {
            System.out.println("No file data available to download.");
            return;
        }
        

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Download Location");
        Stage stage = (Stage) downloadLink.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
   
            String filePath = selectedDirectory.getAbsolutePath() + File.separator + filename;

            File destinationFile = new File(filePath);
            int counter = 1;
            while (destinationFile.exists()) {
          
                String fileNameWithCounter = getFileNameWithCounter(filename, counter);
                filePath = selectedDirectory.getAbsolutePath() + File.separator + fileNameWithCounter;
                destinationFile = new File(filePath);
                counter++;
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
          
                fileOutputStream.write(fileData);

                System.out.println("File downloaded successfully to: " + filePath);
            } catch (IOException e) {
                System.out.println("Error downloading file: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handlestudentSubButton() throws IOException {
        System.out.println(fileNumber + " " + courseID+" "+title);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("submission.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage popupStage = new Stage();
        popupStage.setScene(scene);
        popupStage.setResizable(false);
        popupStage.show();

        subController subController = loader.getController();
   
        subController.setCourseID(courseID);
        subController.setFileNumber(fileNumber);
        subController.init(courseID,fileNumber, title);
    }
    private String getFileNameWithCounter(String filename, int counter) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            // If the filename has no extension
            return filename + "(" + counter + ")";
        } else {
            // If the filename has an extension
            String name = filename.substring(0, dotIndex);
            String extension = filename.substring(dotIndex);
            return name + "(" + counter + ")" + extension;
        }
    }
    public void setDestinationDirectory(String destinationDirectory) {
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }
  
    
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleClose() {
        Stage stage = (Stage) descriptionLabel.getScene().getWindow();
        stage.close();
    }
}
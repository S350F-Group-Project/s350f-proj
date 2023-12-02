package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AssignmentPopupController {
	@FXML
    private Label titleLabel;
    @FXML
    private TextArea descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Hyperlink downloadLink;
    @FXML
    private Button attachFileButton;
    @FXML
    private Button confirmSubmitButton;
    private String destinationDirectory;
    private File selectedFile;
    private String filename;
    private byte[] fileData;
    private byte[] attachedFileData;
    private String courseID;
    private String username;
    private String title;
    private String status;
    java.sql.Date comparedeadline;
    private Connection connection;
    
    public void setAssignmentDetails(String title,String description, String status, String deadline, String filename) {
    	titleLabel.setText(title);
        descriptionLabel.setText(description);
        statusLabel.setText(status);
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

        // Open a directory chooser dialog to select the download location
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Download Location");
        Stage stage = (Stage) downloadLink.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            // Create the file path for the downloaded file
            String filePath = selectedDirectory.getAbsolutePath() + File.separator + filename;

            File destinationFile = new File(filePath);
            int counter = 1;
            while (destinationFile.exists()) {
                // Append a counter to the file name to avoid overwriting
                String fileNameWithCounter = getFileNameWithCounter(filename, counter);
                filePath = selectedDirectory.getAbsolutePath() + File.separator + fileNameWithCounter;
                destinationFile = new File(filePath);
                counter++;
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                // Write the file data to the output stream
                fileOutputStream.write(fileData);

                System.out.println("File downloaded successfully to: " + filePath);
            } catch (IOException e) {
                System.out.println("Error downloading file: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleAttachFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
        	  try {
				attachedFileData = Files.readAllBytes(selectedFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

              String fileName = selectedFile.getName();
              System.out.println("Selected File: " + fileName);
              showAlert(AlertType.INFORMATION, "Success", "File attached successfully.");
        }
    }
   
    @FXML
    private void handleConfirmSubmission(ActionEvent event) throws SQLException {
        if (selectedFile == null) {
            System.out.println("No file selected.");
            showAlert(AlertType.ERROR, "Error", "No file selected.");
            return;
        }
        PreparedStatement psmt= connection.prepareStatement("Select * from submittedassignments where title=? and username=? and courseID=?");
        psmt.setString(1,title);
        psmt.setString(2,username);
        psmt.setString(3,courseID);
        ResultSet result = psmt.executeQuery();
        if(result.next()) {
        	System.out.println("Re-submission is not allowed");
        	showAlert(AlertType.ERROR, "Error", "Re-submission is not allowed");
        	return;
        }
        
        // Generate the submit date as a Timestamp
        Timestamp submitDate = new Timestamp(System.currentTimeMillis());

        // Convert the deadlineDate to a LocalDateTime
        LocalDateTime deadlineDateTime = comparedeadline.toLocalDate().atStartOfDay();
        Timestamp deadlineTimestamp = Timestamp.valueOf(deadlineDateTime);

        // Compare the submit date with the deadline date
        int comparisonResult = submitDate.compareTo(deadlineTimestamp);

        if (comparisonResult > 0) {
            // Handle late submission
            System.out.println("Late Submission");
            // Set the status accordingly
            status = "Late Submission";
        } else {
            // Handle on-time submission
            System.out.println("On-time Submission");
            // Set the status accordingly
            status = "Submitted";
        }
        try {
        	
            // Copy the file to the destination directory
        	//Files.copy(selectedFile.toPath(), new File(destinationFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

            String insertStatement = "INSERT INTO submittedassignments (FILES, FILENAME, COURSEID, USERNAME, SUBMITDATE, TITLE,STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(insertStatement);
            ps.setBytes(1, attachedFileData);
            ps.setString(2, selectedFile.getName());
            ps.setString(3, courseID);
            ps.setString(4, username);
            ps.setTimestamp(5, submitDate);
            ps.setString(6, title);
            ps.setString(7, status); 
          
            
            ps.executeUpdate();

            System.out.println("Submission recorded successfully.");
            showAlert(AlertType.INFORMATION, "Success", "Submission recorded successfully.");
            statusLabel.setText(status);
            // Display a success message to the user or perform any additional operations

        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        this.destinationDirectory = destinationDirectory;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setDeadlineDate(java.sql.Date comparedeadline) {
        this.comparedeadline = comparedeadline;
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
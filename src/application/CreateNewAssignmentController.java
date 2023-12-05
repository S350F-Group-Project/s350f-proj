package application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CreateNewAssignmentController {
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField titleTextField;

    @FXML
    private TextField deadlineTextField;

    private File selectedFile;
    @FXML
    private Hyperlink downloadLink;
    
    @FXML
    private Button confirmCreationButton;
    
    private String fileName;
    
    private Boolean cancel=true;
    public Boolean getCancel() {
        return cancel;
    }
    public String getFileName() {
        return fileName;
    }
    public String getDescription() {
        return descriptionTextArea.getText();
    }
    public String getTitle() {
        return titleTextField.getText();
    }
    public String getDeadline() {
        return deadlineTextField.getText();
    }

    public byte[] getFileData() {
        if (selectedFile != null) {
            try {
            	fileName = selectedFile.getName();
                return Files.readAllBytes(selectedFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile!=null) {
        showAlert(AlertType.INFORMATION, "Success", "File Attached Successfully.");
        }
    }

    @FXML
    private void createAssignment() {
        String title = titleTextField.getText();
        String deadline = deadlineTextField.getText();

        if (title.isBlank()) {
            showAlert(AlertType.ERROR, "Error", "Title cannot be empty");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean validDeadlineFormat = true;

        if (!deadline.isBlank()) {
            try {
                dateFormat.parse(deadline);
            } catch (ParseException e) {
                validDeadlineFormat = false;
            }
        }

        if (!validDeadlineFormat) {
            showAlert(AlertType.ERROR, "Error", "Invalid deadline format. Please follow the format 'yyyy-MM-dd HH:mm:ss'.");
            return;
        }
        
        // Close the popup window
        Stage stage = (Stage) titleTextField.getScene().getWindow();
        cancel=false;
        stage.close();
    }
    
    @FXML
    private void cancel() {
        // Close the popup window without creating a new assignment
    	cancel=true;
        Stage stage = (Stage) titleTextField.getScene().getWindow();
        stage.close();
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
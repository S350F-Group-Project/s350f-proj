package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class subController {
    private Connection connection;
    private String courseID;
    private String fileNumber;
    private String title;

    @FXML
    private TableView<StudentSubmission> tableView;

    @FXML
    private TableColumn<StudentSubmission, String> nameColumn;

    @FXML
    private TableColumn<StudentSubmission, String> idColumn;

    @FXML
    private TableColumn<StudentSubmission, String> submittedFileColumn;

    @FXML
    private TableColumn<StudentSubmission, String> submitDateColumn;

    @FXML
    private TableColumn<StudentSubmission, String> statusColumn;

    @FXML
    private TableColumn<StudentSubmission, String> gradeColumn;

    @FXML
    private Button gradeButton;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public void init(String courseID, String fileNumber, String title) {
        this.courseID = courseID;
        this.fileNumber = fileNumber;
        this.title = title;

        db db = new db();
        try {
            connection = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the cell value factories for each column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        submittedFileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        submitDateColumn.setCellValueFactory(new PropertyValueFactory<>("submitDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Set up the submitted file column to display a hyperlink
        submittedFileColumn.setCellFactory(new Callback<TableColumn<StudentSubmission, String>, TableCell<StudentSubmission, String>>() {
            @Override
            public TableCell<StudentSubmission, String> call(TableColumn<StudentSubmission, String> column) {
                return new TableCell<StudentSubmission, String>() {
                    private final Hyperlink downloadLink = new Hyperlink();

                    {
                        downloadLink.setOnAction(event -> {
                            StudentSubmission submission = getTableRow().getItem();
                            try {
                                downloadSubmissionFile(submission);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String filename, boolean empty) {
                        super.updateItem(filename, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            downloadLink.setText(filename);
                            setGraphic(downloadLink);
                        }
                    }
                };
            }
        });

        // Enable editing for the TableView
        tableView.setEditable(true);

        gradeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gradeColumn.setOnEditCommit(event -> {
            StudentSubmission submission = event.getRowValue();
            submission.setGrade(event.getNewValue());
            uploadGradeToDatabase(submission);
            tableView.refresh();
        });

        // Populate the table view with the student submissions data
        List<StudentSubmission> studentSubmissions = getStudentSubmissionsFromDatabase(connection);
        tableView.setItems(FXCollections.observableArrayList(studentSubmissions));

        // Handle the grade button click event
        gradeButton.setOnAction(event -> {
            uploadGradesToDatabase(tableView.getItems());
        });
    }

    private List<StudentSubmission> getStudentSubmissionsFromDatabase(Connection connection) {
        List<StudentSubmission> studentSubmissions = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM submittedAssignments s ,users u WHERE s.username=u.username AND courseID=? AND title=?");
            statement.setString(1, courseID);
            statement.setString(2, title);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String studentID = resultSet.getString("username");
                String filename = resultSet.getString("FILENAME");
                String submitDate = resultSet.getString("SUBMITDATE");
                String status = resultSet.getString("STATUS");
                String grade = resultSet.getString("GRADE");

                StudentSubmission submission = new StudentSubmission(name, studentID, filename, submitDate, status, grade);
                studentSubmissions.add(submission);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentSubmissions;
    }

    private void downloadSubmissionFile(StudentSubmission submission) throws IOException {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT files FROM submittedAssignments WHERE courseID = ? AND title = ? AND username = ?");
            statement.setString(1, courseID);
            statement.setString(2, title);
            statement.setString(3, submission.getStudentID());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Blob blob = resultSet.getBlob("files");
                InputStream inputStream = blob.getBinaryStream();

                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(new Stage());

                if (selectedDirectory != null) {
                    File file = new File(selectedDirectory.getAbsolutePath() + "/" + submission.getFilename());
                    OutputStream outputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void uploadGradesToDatabase() {
     
        List<StudentSubmission> gradedSubmissions = tableView.getItems();
        uploadGradesToDatabase(gradedSubmissions);
      
    }
    private void uploadGradesToDatabase(List<StudentSubmission> submissions) {
     
        try {
            for (StudentSubmission submission : submissions) {
                PreparedStatement statement = connection.prepareStatement("UPDATE submittedAssignments SET GRADE = ? WHERE courseID = ? AND title = ? AND username = ?");
               	System.out.println(submission.getGrade()+"   "+courseID +"      "+title);
                statement.setString(1, submission.getGrade());
                statement.setString(2, courseID);
                statement.setString(3, title);
                statement.setString(4, submission.getStudentID());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showAlert(AlertType.INFORMATION, "Success", "Assignments Graded Successfully");
    }
    private void uploadGradeToDatabase(StudentSubmission submission) {
    	
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE submittedAssignments SET GRADE = ? WHERE courseID = ? AND title = ? AND username = ?");
            statement.setString(1, submission.getGrade());
            statement.setString(2, courseID);
            statement.setString(3, title);
            statement.setString(4, submission.getStudentID());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
   
}
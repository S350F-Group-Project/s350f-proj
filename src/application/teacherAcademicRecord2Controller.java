package application;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class teacherAcademicRecord2Controller {
private String courseID;
private Connection connection;
@FXML
private ListView<Hyperlink> studentListView;
@FXML
private Button backButton;
private user loggedInUser;
public void setConnection(Connection connection) {
	this.connection=connection;
	
}
public void setCourseID(String courseID) {
    this.courseID=courseID;	
}

public void initialize(Connection connection,String courseID) throws Exception {
	 this.connection=connection;
	 this.courseID=courseID;
	 db db = new db();
	 connection=db.getConnection();
     backButton.setOnAction(event -> {
    	 try {
    	 Node node = (Node) event.getSource();
         Stage stage = (Stage) node.getScene().getWindow();
         this.loggedInUser = (user) stage.getUserData();

    	 FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherAcademicRecords.fxml"));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         DashboardController teacherController=loader.getController();
        
         stage.setScene(scene);
         stage.setResizable(false);
         stage.show();
         teacherController.connection=this.connection;
        
         
			loggedInUser.teacherAcademicRecord(loggedInUser.username, loggedInUser.password, this.connection, teacherController.coursePane, stage, loggedInUser);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
       
    });
    try {
        PreparedStatement statement = connection.prepareStatement("SELECT u.ID, u.name FROM users u INNER JOIN courses c ON u.progID = c.progID WHERE c.CourseID = ?");
        System.out.println(courseID);
        statement.setString(1, courseID);
        ResultSet resultSet = statement.executeQuery();
        
        while (resultSet.next()) {
            String studentID = resultSet.getString("ID");
            String studentName = resultSet.getString("NAME");
            String linkStr= "Name: "+studentName+"   "+ "Student ID: "+studentID;
            Hyperlink studentLink = new Hyperlink(linkStr);
            studentLink.setOnAction(event -> {
           
                try {
                	 Node node = (Node) event.getSource();
                     Stage stage = (Stage) node.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherAcademicRecord3.fxml"));
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
                 // Retrieve the back button from teacherAcademicRecords.fxml
                  
                    Button backButton = (Button) root.lookup("#backButton");

                    // Set the action for the back button
                    backButton.setOnAction(backEvent -> {
                   	 try {
                   		this.loggedInUser = (user) stage.getUserData();
                    	FXMLLoader loader2 = new FXMLLoader(getClass().getResource("teacherAcademicRecords.fxml"));
                        Parent root2 = loader2.load();
                        Scene scene = new Scene(root2);
                        DashboardController teacherController=loader2.getController();
                       
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                        teacherController.connection=this.connection;
                       
                        
               			this.loggedInUser.teacherAcademicRecord(this.loggedInUser.username, this.loggedInUser.password, this.connection, teacherController.coursePane, stage, this.loggedInUser);
               		} catch (SQLException e) {
               			
               			e.printStackTrace();
               		} catch (IOException e) {
               		
               			e.printStackTrace();
               		}
                      
                   });
                
                    PreparedStatement s3 = this.connection.prepareStatement("SELECT username FROM users where id=?");
                    s3.setString(1,studentID);
                    ResultSet r3=s3.executeQuery();
                    r3.next();
                    String username=r3.getString(1);
                    PreparedStatement s2 = this.connection.prepareStatement("SELECT COURSEID,TERM,GRADE,SUBJECT,STATUS,UNIT,TOPSTUDENT FROM AcademicRecords where username=?");
                    s2.setString(1, username);
                    ResultSet r2=s2.executeQuery();
                    List<AcademicRecord> records = new ArrayList<>();
                    while (r2.next()) {
                        String courseid = r2.getString("COURSEID");
                        String term = r2.getString("TERM");
                        String grade = r2.getString("GRADE");
                        String subject = r2.getString("SUBJECT");
                        String status = r2.getString("STATUS");
                        String unit = r2.getString("UNIT");
                        String topStudent = r2.getString("TOPSTUDENT");
                 
                        records.add(new AcademicRecord(term, subject,courseid,status,unit,grade,topStudent));
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
                    
                    
                    teacherAcademicRecord2Controller teacherAcademicRecord2Controller=loader.getController();
                  
                    PreparedStatement statement2 = this.connection.prepareStatement("SELECT CGPA FROM users where username=?");
                    statement2.setString(1, username);
                    ResultSet resultSet2 = statement2.executeQuery();
                    if(resultSet2.next()) {
                    	Label cgpa = (Label) root.lookup("#cgpa");
                    	cgpa.setText(resultSet2.getString(1));
                    }
                 
                    
                    stage.show();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            studentListView.getItems().add(studentLink);
        }

        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



}

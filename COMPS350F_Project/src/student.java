import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class student extends user {

    public student(){
    }
  
    public String getUsername(){
        return this.username;
    }
      public String getRole(){
        return this.role;
    }
      public String getName(){
        return this.name;
    }
      public String getPassword(){
        return this.password;
    }
    public void setAll(String username, String password, String name, String role) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.role = role;

}

    public void myCourse(String username, String password, Connection connection, GridPane coursePane, Stage stage, student loggedInStudent) throws SQLException {
        PreparedStatement progid = connection.prepareStatement("SELECT PROGID FROM students WHERE username = ? AND password=?");
        progid.setString(1, username);
        progid.setString(2, password);
        ResultSet progResult = progid.executeQuery();

        while (progResult.next()) {
            String PROGID = progResult.getString("PROGID");

            // Retrieve the courses for the given PROGID
            PreparedStatement coursesStatement = connection.prepareStatement("SELECT * FROM COURSES WHERE PROGID = ?");
            coursesStatement.setString(1, PROGID);
            ResultSet coursesResultSet = coursesStatement.executeQuery();

            int row = 1;
            // Display the student's courses in the grid pane

            while (coursesResultSet.next()) {
                String courseName = coursesResultSet.getString("COURSENAME");
                String courseID = coursesResultSet.getString("COURSEID");
                String course = "      " + courseID + "  " + courseName;

                // Create a hyperlink for the course
                Hyperlink courseLink = new Hyperlink(course);
                courseLink.setFont(Font.font("Avenir Heavy", 20));

                // Handle the hyperlink click event
                courseLink.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Switch the scene to CourseBoard.fxml
                        try {
                            
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            //student loggedInStudent = (student) stage.getUserData();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseBoard.fxml"));
            
            Parent root = loader.load();
            CourseBoardController courseBoardController = loader.getController();
        
            courseBoardController.setConnection(connection);
          
           courseBoardController.setCourseID(courseID);
           
           courseBoardController.initialize();
       
            
            Scene scene = new Scene(root);
          
            

            
            
            
            //courseBoardController.courseID=courseID;
            //Stage primaryStage = (Stage) courseLink.getScene().getWindow();
            stage.setScene(scene);
             
  
            stage.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Add the hyperlink to the grid pane
              
                coursePane.add(courseLink, 0, row);
                row++;
           
        }
    }
   
            }
}
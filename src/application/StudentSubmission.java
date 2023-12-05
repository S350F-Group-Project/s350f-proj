package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class StudentSubmission {

    private String name;
    private String studentID;
    private String filename;
    private String submitDate;
    private String status;
    private String grade;

    public StudentSubmission(String name, String studentID, String filename, String submitDate, String status, String grade) {
        this.name = name;
        this.studentID = studentID;
        this.filename = filename;
        this.submitDate = submitDate;
        this.status = status;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getFilename() {
        return filename;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public String getStatus() {
        return status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
    }

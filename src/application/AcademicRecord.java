package application;

public class AcademicRecord {
	public String term;
	public String subject;
	public String courseID;
	public String status;
	public String unit;
	public String grade;
    public String topStudent;

    public AcademicRecord(String term, String subject, String courseID, String status, String unit, String grade, String topStudent) {
        this.term = term;
        this.subject = subject;
        this.courseID = courseID;
        this.status = status;
        this.unit = unit;
        this.grade = grade;
        this.topStudent = topStudent;
    }
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTopStudent() {
        return topStudent;
    }

    public void setTopStudent(String topStudent) {
        this.topStudent = topStudent;
    }
}
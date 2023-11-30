module s350f {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires jdk.jdi;
	
	opens application to javafx.graphics, javafx.fxml;
}

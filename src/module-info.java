module s350f {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires jdk.jdi;
	requires javafx.graphics;
	requires javafx.base;
	requires java.desktop;
	requires javafx.swing;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}

package application;

public class userModel {
    private String id;
    private String name;
    private String username;
    private String password;
    private String role;

        public userModel(String id, String name, String username, String password, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }
        public String getRole() {
            return role;
        }
        public String getName() {
            return name;
        }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public String getId() {
            return id;
        }
        public void setPassword(String pw) {
        	this.password=pw;
        }
        public void setRole(String role) {
        	this.role=role;
        	
        }

    // Add getters and setters for each field
    // ...
}
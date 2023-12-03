package application;

import java.sql.Connection;
import java.sql.DriverManager;


public class db {
    private Connection connection=null;
    
    public Connection getConnection()throws Exception{
 
     String DB_URL = "jdbc:oracle:thin:@oracleacademy.ouhk.edu.hk:8998:db1011";
     String DB_USERNAME = "s1289505";
     String DB_PASSWORD = "12895052";
    connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    return connection;
     }
    
}

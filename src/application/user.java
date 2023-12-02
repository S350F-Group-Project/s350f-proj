package application;

abstract public class user {
    protected String username;
    protected String password;
    protected String name;
    protected String role;

  
    
    public user(String username,String password,String name,String role ){
        this.username=username;
        this.password=password;
        this.role=role;
        this.name=name;
  
    }
    public user(){
        this.username=null;
        this.password=null;
        this.name=null; 
        this.role=null;
     
        
    }
    
    
}

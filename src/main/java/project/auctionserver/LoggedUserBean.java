package project.auctionserver;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import project.dal.UnitOfWork;
import project.domain.UserProfile;

@Named(value = "loggedUserBean")
@SessionScoped
public class LoggedUserBean implements Serializable {

    private boolean isLogin;
    private String userName;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String errLoginMessage;
    private String errRegisterMessage;
    
    public LoggedUserBean() {
        isLogin = false;
    }
    
    public boolean getIsLogin() {
        return isLogin;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getErrLoginMessage() {
        return errLoginMessage;
    }
    
    public String getErrRegisterMessage() {
        return errRegisterMessage;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void logout() {
        isLogin = false;
        userName = null;
        password = null;
        errLoginMessage = null;
    }
    
    public String login() {
        if (true) {     //(isRegistered(userName, password))
            isLogin = true;
            errLoginMessage = null;
            return "mainMenu.xhtml";
        }
        else {
            password = null;
            errLoginMessage = "Name or Password not correct";
            return "login.xhtml";
        }
    }
    
    public String register() {
        
        UserProfile user = new UserProfile("yaniv", "Yaniv", "Shalom", "111", "3335");
        UnitOfWork unitOfWork = UnitOfWork.create();
        unitOfWork.persist(user);
        unitOfWork.saveChanges();
        unitOfWork.close();
        
        
        if (true) 
        
        
            errRegisterMessage= "my error";
        
        return "register.xhtml";
        
        
           
        
        //return "mainMenu.xhtml";
    }
    
    
}

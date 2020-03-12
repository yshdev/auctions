/************************************************************
 * LoggedUserBean.java
 * 
 * Java bean to manage the logged user, Login and Registration
 ************************************************************/
package project.auctionserver;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import project.dal.UnitOfWork;
import project.domain.UserProfile;

@Named(value = "loggedUserBean")
@SessionScoped
public class LoggedUserBean implements Serializable {

    // user connected
    private UserProfile connectedUser;
    
    // JSF input fields
    private String userName;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
    // error messages
    private String errLoginMessage;
    private String errRegisterMessage;
    
    public LoggedUserBean() {
        connectedUser = null;
    }
    
    public UserProfile getConnectedUser() {
        return connectedUser;
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
    
    /* when user logged out, change system status by changing
       isLogin to false. nullify error messages.*/
    public String logout() {
        connectedUser = null;
        errLoginMessage = null;
        errRegisterMessage = null;
        
        return "mainMenu.xhtml";
    }
    
    /* when user wants to login, compare input name and password
       with database and accordingly change system status or print
       an error message.*/
    public String login() {
        if (userName.equals("aharon")) {
        /*******************************************************************
         * temporary condition. need to be changed to compare input to DB:
         *      if (isRegistered(userName, password))
         *******************************************************************/
            connectedUser = null; // !!! get from DB currect UserProfile !!!!
            errLoginMessage = null;
            errRegisterMessage = null;
            return "mainMenu.xhtml";
        }
        else {
            password = null;
            errLoginMessage = "Name or Password not correct";
            return "login.xhtml";
        }
    }
   
    /* when new user wants to register, ensure he is not registered
       yet.*/
    public String register() {
        
        UnitOfWork unitOfWork = UnitOfWork.create();
        boolean isRegistered = unitOfWork.isRegistered(email);
        unitOfWork.close();
        
        if (!isRegistered) { // not registered yet
        
            UserProfile user = new UserProfile("yaniv", "Yaniv", "Shalom", "111", "3335");
            unitOfWork = UnitOfWork.create();
            unitOfWork.persist(user);
            unitOfWork.saveChanges();
            unitOfWork.close();
            
            connectedUser = user;
            return "mainMenu.xhtml";
        }
        else {
            errRegisterMessage= "you are registered already";
            return "register.xhtml";
        }
    }
}

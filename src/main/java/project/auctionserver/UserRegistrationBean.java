/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.HashAndSaltPair;
import project.domain.Security;
import project.domain.UserProfile;
import project.service.Mapper;

/**
 *
 * @author Shalom
 */

@Named
@ViewScoped
public class UserRegistrationBean implements Serializable {
    
    @Inject
    private LoggedUserBean loggedUserBean;
    
    private final Mapper mapper = new Mapper();
    
    private String userName;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String error;
    
    public UserRegistrationBean() {
    }

    public LoggedUserBean getLoggedUserBean() {
        return loggedUserBean;
    }

    
    public void setLoggedUserBean(LoggedUserBean loggedUserBean) {
        this.loggedUserBean = loggedUserBean;
    }

    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    
     /* when new user wants to register, ensure he is not registered yet.*/
    public void register() throws IOException {
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
        
            boolean isUnique = unitOfWork.isUniqueUsername(userName);
            if (isUnique) { // not registered yet
        
                HashAndSaltPair hashAndSalt = Security.hash(this.password);
                
                UserProfile user = new UserProfile(this.userName, this.firstName, this.lastName, this.email, this.phone, hashAndSalt.getHash(), hashAndSalt.getSalt());
                
                unitOfWork.persist(user);
                unitOfWork.saveChanges();
                
                if (this.loggedUserBean == null) { 
                    this.loggedUserBean =  (LoggedUserBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUserBean");
                }
                
            
                 this.loggedUserBean.setUser(this.mapper.mapUserToDto(user));
                 
                 FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
            }
            else {
                this.error = "The username is already taken. Try another.";
            }
        }
    }
}
    


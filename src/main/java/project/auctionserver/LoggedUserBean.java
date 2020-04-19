/** **********************************************************
 * LoggedUserBean.java
 *
 * Java bean to manage the logged user, Login and Registration
 *********************************************************** */
package project.auctionserver;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import project.dal.UnitOfWork;
import project.domain.HashAndSaltPair;
import project.domain.UserProfile;
import project.domain.Security;
import project.service.Mapper;
import project.service.UserDto;

@Named
@SessionScoped
public class LoggedUserBean implements Serializable {

    private UserDto user;
    private String error;
    private String inputUsername;
    private String inputPassword;
    private final Mapper mapper = new Mapper();
    private String referrer;

    public LoggedUserBean() {
    }

    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }

    public Integer getUserId() {
        return this.user == null ? null : this.user.getId();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getInputUsername() {
        return inputUsername;
    }

    public void setInputUsername(String inputUsername) {
        this.inputUsername = inputUsername;
    }

    public String getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(String inputPassword) {
        this.inputPassword = inputPassword;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
    
    public void redirectToLogin(String referrer) throws IOException {
        this.referrer = referrer;
        
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
                
    }

    public String logout() {
        this.user = null;
        this.error = null;

        return "home.xhtml";
    }

    /* when user wants to login, compare input name and password
       with database and accordingly change system status or print
       an error message.*/
    public void login() throws IOException {

        try ( UnitOfWork unitOfWork = UnitOfWork.create()) {

            UserProfile newUser = unitOfWork.findUserByUsername(this.inputUsername);
            boolean isAuthenticated = Security.authenticate(this.inputPassword, new HashAndSaltPair(newUser.getPasswordHash(), newUser.getPasswordSalt()));
            String url = null;
            if (isAuthenticated) {
                this.user = this.mapper.mapUserToDto(newUser);
                this.error = null;
                if (this.referrer != null) { 
                    url = this.referrer;
                }
                else {
                    url = "home.xhtml";
                }
            } else {
                this.inputPassword = null;
                this.error = "Name or Password not correct";
                url = "login.xhtml";
            }
            
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        }
    }
}

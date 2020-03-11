/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Shalom
 */
@Entity
@NamedQueries({
    @NamedQuery (
        name = "findUserByEmailParam",
        query = "SELECT user FROM UserProfile user WHERE user.email = :inputEmail"
    )
})
public class UserProfile implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String passwordSalt;
    
    @OneToMany(mappedBy = "bidder")
    private List<Bid> bids = new ArrayList<Bid>();
    
    
    public UserProfile()
    {
    }
    
    public UserProfile(String username, String firstName, String lastName, String passwordHash, String passwordSalt)
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }
    

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }
    
    public String getFirstName() {
        return firstName;
    }
 
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    } 
 
    public List<Bid> getBids() {
        return bids;
    }
    
    public void update(String firstName, String lastName) {
        // TODO: Validate
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public void changePassword(String passwordHash, String passwordSalt) {
        // TODO: Validate
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }
    
}

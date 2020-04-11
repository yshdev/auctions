/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Shalom
 */
@Entity
public class Bid implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "fk_auction")
    private Auction auction;
    
    @ManyToOne
    @JoinColumn(name = "fk_userprofile")
    private UserProfile bidder;
    
    private Date timestamp;
    private BigDecimal ammount;

    // For JPA only
    public Bid(){
    }
    
    public Bid(Auction auction, UserProfile user, BigDecimal ammount, Date timestamp){
        this.auction = auction;
        this.bidder = user;
        this.ammount = ammount;
        this.timestamp = timestamp;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Auction getAuction() {
        return auction;
    }

    public UserProfile getBidder() {
        return bidder;
    }

    public BigDecimal getAmount() {
        return this.ammount;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bid) {
            Bid other = (Bid)obj;
            return this.id == other.id;
        }
        return false;    
    }

    @Override
    public int hashCode() {
        return ((Integer)this.id).hashCode(); 
    }
 

    @Override
    public String toString() {
        return "project.domain.Bid[ id=" + id + " ]";
    }
    
}

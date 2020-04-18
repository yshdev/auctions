/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
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
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;
    
    private BigDecimal amount;

    // For JPA only
    public Bid(){
    }
    
    public Bid(Auction auction, UserProfile user, BigDecimal amount, LocalDateTime timestamp){
        
        if (auction == null) {
            throw new IllegalArgumentException("Bid auction cannot be null.");
        }
        
        if (user == null) {
            throw new IllegalArgumentException("Bidder cannot be null.");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Bid amount cannot be negative.");
        }
        
        this.auction = auction;
        this.bidder = user;
        this.amount = amount;
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
        return this.amount;
    }

    public LocalDateTime getTimestamp() {
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

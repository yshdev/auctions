/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Shalom
 */
@Entity
public class Auction implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    private String title;
    private String description;
    private BigDecimal startingAmount;
    private BigDecimal reservedPrice;
    private BigDecimal winningAmount;
    private Date startingTime;
    private Date endingTime;
    private boolean isClosed;
    
    @OneToOne
    private Bid highestBid;
    
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile owner;
    
    @OneToMany(mappedBy = "auction")
    private List<Bid> bids = new ArrayList<Bid>();

    // For
    public Auction() {
    }

    public Auction(UserProfile owner, String title, String description, Date startingTime, int numOfDays, BigDecimal startingAmount,
            BigDecimal winningAmount, BigDecimal reservedPrice) {
        
        if (owner == null) {
            throw new IllegalArgumentException("Auction owner must be set.");
        }
        this.owner = owner;
        this.setTitle(title);
        this.setTimes(startingTime, numOfDays);
        this.setAmounts(startingAmount, winningAmount, reservedPrice);
        this.setDescription(description);
        this.isClosed = false;
    }
   
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        
        if (title == null) {
            throw new IllegalArgumentException("Auction title must be set");
        }
            
        title = title.trim();
        if (title.length() == 0) {
            throw new IllegalArgumentException("Auction title cannot be empty.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public UserProfile getOwner() {
        return owner;
    }

    public BigDecimal getStartingAmount() {
        return startingAmount;
    }
    
    public BigDecimal getReservedPrice() {
        return reservedPrice;
    }

    public BigDecimal getWinningAmount() {
        return winningAmount;
    }

    public Bid getHighestBid() {
        return highestBid;
    }
    
    public void setTimes(Date startingTime, int numOfDays) {
        this.assertCanModify();
        
         if (startingTime == null) {
            throw new IllegalArgumentException("Auction starting time must be set.");
        }
        
        if (numOfDays <= 0) {
            throw new IllegalArgumentException("Auction number of days must be positive.");
        }
    }
    
    public void setAmounts(BigDecimal startingAmount, BigDecimal winningAmount, BigDecimal reservedPrice) {
        
        this.assertCanModify();
        
        if (startingAmount == null) {
            throw new IllegalArgumentException("Auction starting amount must be set.");
        }
        
        if (startingAmount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Auction starting amount must not be negative.");
        }
        
        if (winningAmount != null && winningAmount.compareTo(startingAmount) < 0) {
            throw new IllegalArgumentException("Auction winning amount must not be lower than starting amount.");
        }
        
        if (reservedPrice != null && reservedPrice.compareTo(startingAmount) < 0) {
            throw new IllegalArgumentException("Auction reserved price must be greater than starting amount.");
        } 
        
         this.startingAmount = startingAmount;
        this.reservedPrice = reservedPrice;
        this.winningAmount = winningAmount;
    }
    
    public void addBid(UserProfile bidder, BigDecimal amount) {
        
        if (this.isClosed) {
            throw new IllegalStateException("Auction is closed. Cannot add bids.");
        }
        
        if (bidder == null){
            throw new IllegalArgumentException("Bidder must be set");
        }
        
        if (amount == null) {
            throw new IllegalArgumentException("Bid amount must be set.");
        }
        
        Bid bid = new Bid(this, bidder, amount, new Date());
        
        if (bid.getAmmount().compareTo(this.startingAmount) < 0) {
            throw new IllegalArgumentException("Bid amount must not be lower than starting amount.");
        }
        
        if (this.highestBid != null)
        {
            if (bid.getAmmount().compareTo(this.highestBid.getAmmount()) < 0) {
                throw new IllegalArgumentException("Bid amount must be greater than highest bid.");
            }
            
            this.highestBid = bid;
        }
        
        this.bids.add(bid);
    }
    
    private void assertCanModify() {
        if (this.isClosed) {
            throw new IllegalStateException("Auction is closed. Cannot modify.");
        }
        
        if (this.startingTime.compareTo(new Date()) < 0) {
            throw new IllegalStateException("Auction has already started. Cannot modify.");
        }
    }
            
}

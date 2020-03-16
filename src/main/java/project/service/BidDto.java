/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Shalom
 */
public class BidDto {
        
    private int id;
    private UserDto bidder;
    private Date timestamp;
    private BigDecimal ammount;
    
    public BidDto(){
    }
    
    public BidDto(int id, UserDto bidder, Date timestamp, BigDecimal ammount) {
        this.id = id;
        this.bidder = bidder;
        this.timestamp = timestamp;
        this.ammount = ammount;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDto getBidder() {
        return bidder;
    }

    public void setBidder(UserDto bidder) {
        this.bidder = bidder;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmmount() {
        return ammount;
    }

    public void setAmmount(BigDecimal ammount) {
        this.ammount = ammount;
    }
    
    
}

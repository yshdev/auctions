/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author Shalom
 */
public class BidDto {
        
    private int id;
    private UserDto bidder;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    
    public BidDto(){
    }
    
    public BidDto(int id, UserDto bidder, LocalDateTime timestamp, BigDecimal amount) {
        this.id = id;
        this.bidder = bidder;
        this.timestamp = timestamp;
        this.amount = amount;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getTimestampText() {
        return this.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    
}

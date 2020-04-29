/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import project.domain.AuctionStatus;

/**
 *
 * @author Shalom
 */
public class AuctionDetailsDto extends AuctionListItemDto {
    
    private String description;
    private BigDecimal winningAmount;
    
    private BigDecimal reservedPrice;
    private LocalDateTime startingTime;
    private LocalDateTime closingTime;
    private LocalDateTime actualClosingTime;
    private int numberOfBids;
    
    private BidDto highestBid;
    private BidDto winningBid;
    private UserDto owner;
    private BigDecimal minimalBidAmount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartingTime() {
        return startingTime;
    }
    
    public String getStartingTimeText() {
        return this.startingTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }
    
    public String getClosingTimeText() {
        return this.closingTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }
    
    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }

    public LocalDateTime getActualClosingTime() {
        return actualClosingTime;
    }
    
    public String getActualClosingTimeText() {
        return this.actualClosingTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public void setActualClosingTime(LocalDateTime actualClosingTime) {
        this.actualClosingTime = actualClosingTime;
    }

    public BidDto getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(BidDto highestBid) {
        this.highestBid = highestBid;
    }

    public BidDto getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(BidDto winningBid) {
        this.winningBid = winningBid;
    }
    
    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public int getNumberOfBids() {
        return numberOfBids;
    }

    public void setNumberOfBids(int numberOfBids) {
        this.numberOfBids = numberOfBids;
    }

    public BigDecimal getMinimalBidAmount() {
        return minimalBidAmount;
    }

    public void setMinimalBidAmount(BigDecimal minimalBidAmount) {
        this.minimalBidAmount = minimalBidAmount;
    }

    public boolean getIsNotOpennedYet() {
        return this.getStatus() == AuctionStatus.NOT_OPENNED_YET;
    }
    
    public boolean getIsOpen() {
        return this.getStatus() == AuctionStatus.OPEN;
    }
    
    public boolean getIsCanceled() {
        return this.getStatus() == AuctionStatus.CANCELED;
    }
    
    public BigDecimal getWinningAmount() {
        return winningAmount;
    }

    public void setWinningAmount(BigDecimal winningAmount) {
        this.winningAmount = winningAmount;
    }

    public BigDecimal getReservedPrice() {
        return reservedPrice;
    }

    public void setReservedPrice(BigDecimal reservedPrice) {
        this.reservedPrice = reservedPrice;
    }
}
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
public class AuctionDetailsDto {
    private int id;
    private String title;
    private String description;
    private BigDecimal startingAmount;
    private LocalDateTime startingTime;
    private LocalDateTime closingTime;
    private LocalDateTime actualClosingTime;
    private int numberOfBids;
    
    private CategoryDto category;
    private BidDto highestBid;
    private UserDto owner;
    private BigDecimal winningBidAmount;
    private BigDecimal minimalBidAmount;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canBid;
    private boolean userIsOwner;
    private AuctionStatus status;
    
    private BigDecimal userBidAmount;
    private LocalDateTime userBidTimestamp;
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(BigDecimal startingAmount) {
        this.startingAmount = startingAmount;
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

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

      

    public BidDto getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(BidDto highestBid) {
        this.highestBid = highestBid;
    }

    

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public BigDecimal getWinningBidAmount() {
        return winningBidAmount;
    }

    public void setWinningBid(BigDecimal winningBid) {
        this.winningBidAmount = winningBid;
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
     
    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanBid() {
        return canBid;
    }

    public void setCanBid(boolean canBid) {
        this.canBid = canBid;
    }

    public boolean getUserIsOwner() {
        return userIsOwner;
    }

    public void setUserIsOwner(boolean userIsOwner) {
        this.userIsOwner = userIsOwner;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public boolean getIsClosed() {
        return this.status.compareTo(AuctionStatus.CLOSING) >= 0;
    }
    
    public boolean getIsNotOpennedYet() {
        return this.status == AuctionStatus.NOT_OPENNED_YET;
    }
    
    public boolean getIsOpen() {
        return this.status == AuctionStatus.OPEN;
    }
    
    public boolean getIsCanceled() {
        return this.status == AuctionStatus.CANCELED;
    }

    public BigDecimal getUserBidAmount() {
        return userBidAmount;
    }

    public void setUserBidAmount(BigDecimal userBidAmount) {
        this.userBidAmount = userBidAmount;
    }

    public LocalDateTime getUserBidTimestamp() {
        return userBidTimestamp;
    }

    public void setUserBidTimestamp(LocalDateTime userBidTimestamp) {
        this.userBidTimestamp = userBidTimestamp;
    }
    
    public boolean getHasUserBid() {
        return this.userBidAmount != null;
    }
    
    public String getUserBidTimestampText() {
        return this.userBidTimestamp.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }
    
}

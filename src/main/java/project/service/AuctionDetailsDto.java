/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private boolean isClosed;
    private boolean isCanceled;
    private CategoryDto category;
    private BidDto highestBid;
    private UserDto owner;
    private BigDecimal winningBidAmount;
    private BigDecimal minimalBidAmount;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canBid;
    private boolean userIsOwner;
    

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

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }

    public LocalDateTime getActualClosingTime() {
        return actualClosingTime;
    }

    public void setActualClosingTime(LocalDateTime actualClosingTime) {
        this.actualClosingTime = actualClosingTime;
    }

    

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
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
    
    
}

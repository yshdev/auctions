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
public class AuctionDtoForNonOwner  {
    private int id;
    private String title;
    private String description;
    private BigDecimal startingAmount;
    private Date startingTime;
    private Date endingTime;
    private int numberOfBids;
    private boolean isClosed;
    private CategoryDto category;
    private BigDecimal highestBidAmount;
    private UserDto owner;
    private BigDecimal winningBidAmount;
    private BigDecimal minimalBidAmount;
    

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

    public Date getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Date startingTime) {
        this.startingTime = startingTime;
    }

    public Date getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Date endingTime) {
        this.endingTime = endingTime;
    }

    public boolean isClosed() {
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

    public BigDecimal getHighestBidAmount() {
        return highestBidAmount;
    }

    public void setHighestBidAmount(BigDecimal highestBid) {
        this.highestBidAmount = highestBid;
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
     
}

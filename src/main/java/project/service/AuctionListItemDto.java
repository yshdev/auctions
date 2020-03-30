/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;

/**
 *
 * @author Shalom
 */
public class AuctionListItemDto {
 
    private int id;
    private String title;
    private CategoryDto category;
    private BigDecimal latestBidAmount;
    private boolean canCancel;
    private boolean canEdit;
    private boolean isClosed;
    private BigDecimal startingAmount;

    public AuctionListItemDto() {
    }

    public AuctionListItemDto(int id, String title, CategoryDto category, BigDecimal startingAmmount, BigDecimal latestBidAmount) {
        this.id  = id;
        this.title = title;
        this.category = category;
        this.latestBidAmount = latestBidAmount;
        this.startingAmount = startingAmmount;
        
    }

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

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public BigDecimal getLatestBidAmount() {
        return latestBidAmount;
    }

    public void setLatestBidAmount(BigDecimal latestBidAmount) {
        this.latestBidAmount = latestBidAmount;
    }

    public boolean canCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean canEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public BigDecimal getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(BigDecimal startingBid) {
        this.startingAmount = startingBid;
    }
    
    
}

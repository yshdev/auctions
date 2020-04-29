/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import project.domain.AuctionStatus;


/**
 *
 * @author Shalom
 */
public class AuctionListItemDto {
 
    private int id;
    private BigDecimal latestBidAmount;
    private String title;
    private CategoryDto category;
    private boolean canCancel;
    private boolean canEdit;
    private boolean canBid;
    private BigDecimal startingAmount;
    private byte[] imageBytes;
    private BigDecimal userBidAmount;
    private LocalDateTime userBidTimestamp;
    private boolean userIsOwner;
    private boolean userIsWinner;
    private boolean userIsNotWinner;
    private AuctionStatus status;

    public AuctionListItemDto() {
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

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getIsClosed() {
        return this.status.compareTo(AuctionStatus.CLOSING) >= 0;
    }

    public BigDecimal getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(BigDecimal startingBid) {
        this.startingAmount = startingBid;
    }

    public boolean getCanBid() {
        return canBid;
    }

    public void setCanBid(boolean canBid) {
        this.canBid = canBid;
    }

    public StreamedContent getImage() {
        
        if (this.imageBytes == null) {
            return null;
        }
        
         return DefaultStreamedContent.builder().contentType("image/jpeg")
                .stream(() -> new ByteArrayInputStream(this.imageBytes))
                .build();
    }
    
    public void setImageBytes(byte[] bytes) {
        this.imageBytes =  bytes;
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

        return this.userBidTimestamp == null? null : this.userBidTimestamp.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }
    
    public boolean getUserIsWinner() {
        return userIsWinner;
    }

    public void setUserIsWinner(boolean userIsWinner) {
        this.userIsWinner = userIsWinner;
    }

    public boolean getUserIsNotWinner() {
        return userIsNotWinner;
    }

    public void setUserIsNotWinner(boolean userIsNotWinner) {
        this.userIsNotWinner = userIsNotWinner;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public boolean getUserIsOwner() {
        return userIsOwner;
    }

    public void setUserIsOwner(boolean userIsOwner) {
        this.userIsOwner = userIsOwner;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import project.dal.ImageUtils;
import project.service.AuctionDetailsDto;

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

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startingTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime closingTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime actualClosingTime;

    private boolean isCanceled;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne
    private Bid highestBid;

    @OneToOne
    private Bid winningBid;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserProfile owner;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private List<Bid> bids = new ArrayList<Bid>();

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] picture;
    
    // For
    public Auction() {
    }

    public Auction(UserProfile owner, Category category, String title, LocalDate startingDate, int startingHour, int numOfDays, BigDecimal startingAmount,
            BigDecimal winningAmount, BigDecimal reservedPrice) {

        if (owner == null) {
            throw new IllegalArgumentException("Auction owner must be set.");
        }

        if (category == null) {
            throw new IllegalArgumentException("Auction category cannot be null.");
        }

        this.validateTitle(title);
        this.validateTimes(startingDate, startingHour, numOfDays);
        this.validateAmounts(startingAmount, winningAmount, reservedPrice);

        this.owner = owner;
        this.category = category;
        this.title = title;
        this.startingTime = startingDate.atTime(startingHour, 0);
        this.closingTime = this.startingTime.plusDays(numOfDays);
        this.startingAmount = startingAmount;
        this.winningAmount = winningAmount;
        this.reservedPrice = reservedPrice;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
    
    public Bid getWinningBid() {
        return winningBid;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }
    
    public int getNumOfDays() {
        return (int)this.startingTime.until(closingTime, ChronoUnit.DAYS);
    }

    public LocalDateTime getActualClosingTime() {
        return actualClosingTime;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isClosed() {
        return this.getStatus().compareTo(AuctionStatus.CLOSING) >= 0;
    }

    public byte[] getImageBytes() {
        return this.picture;
    }

    public boolean canCancel(Integer userId) {
        return userId != null && this.getStatus() == AuctionStatus.NOT_OPENNED_YET && this.owner.getId() == userId;
    }

    public boolean canEdit(Integer userId) {
        return userId != null && this.getStatus() == AuctionStatus.NOT_OPENNED_YET && this.owner.getId() == userId;
    }

    public boolean canBid(Integer userId) {
        return userId != null && this.getStatus() == AuctionStatus.OPEN && this.owner.getId() != userId;
    }
    
     public AuctionStatus getStatus() {
         
        if (this.isCanceled) {
            return AuctionStatus.CANCELED;
        }

        if (this.actualClosingTime != null) {
            return AuctionStatus.CLOSED;
        }

        if (this.closingTime.compareTo(LocalDateTime.now()) < 0) {
            return AuctionStatus.CLOSING;
        }

        if (this.startingTime.compareTo(LocalDateTime.now()) < 0) {
            return AuctionStatus.OPEN;
        }

        return AuctionStatus.NOT_OPENNED_YET;
    }
    
    public BigDecimal getMinimalBidAmount() {
        
        if (this.getHighestBid() == null) {
            return this.getStartingAmount();
        }
        
        double sa = this.startingAmount.doubleValue();
        int jump = Math.max(1, (int)Math.log10(sa)) * 5;
        
        BigDecimal minBid = this.getHighestBid().getAmount().add(new BigDecimal(jump));
                
        return minBid;
    }
    
    public void setCategory(Category category) {

        if (category == null) {
            throw new IllegalArgumentException("Auction category cannot be empty.");
        }
        this.category = category;
    }

    public void setTitle(String title) {

        this.assertCanModify();
        this.validateTitle(title);
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(BufferedImage picture, String imageType) throws IOException {
        if (picture == null) {
            this.picture = null;
        }
        this.picture = ImageUtils.convertImageToBytes(picture, imageType);
    }
    
    public void setPicture(byte[] buffer) {
        this.picture = buffer;
    }

    public void setTimes(LocalDate startingDate, int hour, int numOfDays) {
        this.assertCanModify();
        this.validateTimes(startingDate, hour, numOfDays);

        this.startingTime = startingDate.atTime(hour, 0);
        this.closingTime = this.startingTime.plusDays(numOfDays);
    }

    public void setAmounts(BigDecimal startingAmount, BigDecimal winningAmount, BigDecimal reservedPrice) {

        this.assertCanModify();

        this.validateAmounts(startingAmount, winningAmount, reservedPrice);

        this.startingAmount = startingAmount;
        this.reservedPrice = reservedPrice;
        this.winningAmount = winningAmount;
    }

    public Bid addBid(UserProfile bidder, BigDecimal amount) {

        AuctionStatus state = this.getStatus();
        if (state == AuctionStatus.NOT_OPENNED_YET) {
            throw new IllegalStateException("Auction is not open yet. Cannot add bids.");
        }

        if (state.compareTo(AuctionStatus.CLOSING) >= 0) {
            throw new IllegalStateException("Auction is closed. Cannot add bids.");
        }

        if (bidder == null) {
            throw new IllegalArgumentException("Bidder must be set");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Bid amount must be set.");
        }
        
        BigDecimal minAmount = this.getMinimalBidAmount();
        
        if (amount.compareTo(minAmount) < 0) {
            throw new IllegalArgumentException("Bid amount must not be lower than minimal amount.");
        }

        Bid bid = new Bid(this, bidder, amount, LocalDateTime.now(), true);

        if (this.highestBid != null) {
            if (bid.getAmount().compareTo(this.highestBid.getAmount()) < 0) {
                throw new IllegalArgumentException("Bid amount must be greater than highest bid.");
            }

            this.highestBid = bid;
        }
        else {
            this.highestBid = bid;
        }

        this.bids.add(bid);

        if (this.winningAmount != null && bid.getAmount().compareTo(this.winningAmount) >= 0) {
            this.setResult(bid, false);
        }
        
        return bid;
    }

    public void cancel() {
        if (this.getStatus().compareTo(AuctionStatus.CLOSING) >= 0) {
            throw new IllegalStateException("Auction has already closed. Cannot cancel.");
        }
        this.setResult(null, true);
    }

    public boolean canClose() {
        return this.getStatus() == AuctionStatus.CLOSING;
    }

    public void close() {
        if (!this.canClose()) {
            throw new IllegalStateException("Auction cannot be closed.");
        }

        Bid winningBid = null;
        if (this.highestBid != null
                && (this.reservedPrice == null || this.reservedPrice.compareTo(this.highestBid.getAmount()) < 0)) {
            winningBid = this.highestBid;
        }

        this.setResult(winningBid, false);
    }

   

    private void assertCanModify() {

        AuctionStatus state = this.getStatus();

        if (state.compareTo(AuctionStatus.CLOSING) >= 0) {
            throw new IllegalStateException("Auction is closed. Cannot modify.");
        }

        if (state == AuctionStatus.OPEN) {
            throw new IllegalStateException("Auction has already started. Cannot modify.");
        }
    }

    private void setResult(Bid winningBid, boolean isCanceled) {
        this.isCanceled = isCanceled;
        this.winningBid = winningBid;
        this.actualClosingTime = LocalDateTime.now();
    }

    private void validateTimes(LocalDate startingDate, int hour, int numOfDays) {
        if (startingDate == null) {
            throw new IllegalArgumentException("Auction starting date must be set.");
        }

        LocalDateTime startingTimestamp = startingDate.atTime(hour, 0);

        if (startingTimestamp.compareTo(LocalDateTime.now()) < 0) {
            throw new IllegalArgumentException("Auction starting time must be greater than now.");
        }

        if (numOfDays <= 0) {
            throw new IllegalArgumentException("Auction number of days must be positive.");
        }
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Auction title must be set");
        }

        title = title.trim();

        if (title.isEmpty()) {
            throw new IllegalArgumentException("Auction title cannot be empty.");
        }
    }

    private void validateAmounts(BigDecimal startingAmount, BigDecimal winningAmount, BigDecimal reservedPrice) {

        if (startingAmount == null) {
            throw new IllegalArgumentException("Auction starting amount must be set.");
        }

        if (startingAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Auction starting amount must not be negative.");
        }

        if (winningAmount != null && winningAmount.compareTo(startingAmount) < 0) {
            throw new IllegalArgumentException("Auction winning amount must not be lower than starting amount.");
        }

        if (reservedPrice != null && reservedPrice.compareTo(startingAmount) < 0) {
            throw new IllegalArgumentException("Auction reserved price must be greater than starting amount.");
        }
    }
}

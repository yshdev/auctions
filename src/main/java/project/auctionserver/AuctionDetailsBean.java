/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.Auction;
import project.domain.Bid;
import project.domain.UserProfile;
import project.service.AuctionDetailsDto;
import project.service.Mapper;

/**
 *
 * @author Shalom
 */
@Named
@ViewScoped
public class AuctionDetailsBean implements Serializable {

    @Inject
    private LoggedUserBean loggedUserBean;
    private Integer auctionId;
    private final Mapper mapper = new Mapper();
    private String error;
    private AuctionDetailsDto auction;
    private BigDecimal bidAmount;

    public LoggedUserBean getLoggedUserBean() {
        return loggedUserBean;
    }

    public void setLoggedUserBean(LoggedUserBean loggedUserBean) {
        this.loggedUserBean = loggedUserBean;
    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
        this.refresh();
    }

    public AuctionDetailsDto getAuction() {
        return auction;
    }

    public void setAuction(AuctionDetailsDto auction) {
        this.auction = auction;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    

    public String cancelAuction() {
        try ( UnitOfWork unitOfWork = UnitOfWork.create()) {
            Auction auction = unitOfWork.findAuction(this.auctionId);
            if (auction == null) {
                this.error = "Auction not found!";
                this.auction = null;
            } else {
                this.error = null;
                if (auction.canCancel(this.loggedUserBean.getUserId())) {
                    auction.cancel();
                    unitOfWork.saveChanges();
                } else {
                    this.error = "Cannot cancel auction!";
                }
            }
        }
        
        if (this.error == null) {
            this.refresh();
        }
        return "private.xhtml";
    }

    /**
     *
     * @param amount
     */
    public void bid() {
        try {
            try ( UnitOfWork unitOfWork = UnitOfWork.create()) {

                UserProfile user = unitOfWork.findUserById(this.loggedUserBean.getUserId());
                Auction auction = unitOfWork.findAuction(this.auctionId);
                
                Bid lastBid = unitOfWork.findLastUserBid(auction.getId(), user.getId());
                auction.addBid(user, this.bidAmount);
                if (lastBid != null) {
                    lastBid.setIsUserHighest(false);
                }
                unitOfWork.saveChanges();
            }
        } catch (IllegalArgumentException ax) {
            this.error = ax.getMessage();
        }
        
        if (this.error == null) {
            this.refresh();
        }
    }

    private void refresh() {

        try ( UnitOfWork unitOfWork = UnitOfWork.create()) {
            Auction auction = unitOfWork.findAuction(this.auctionId);
            if (auction == null) {
                this.error = "Auction not found!";
                this.auction = null;
            } else {
                this.error = null;
                
                Bid userBid = null;
                Integer userId = this.loggedUserBean.getUserId();
                if (userId != null) {
                    userBid = unitOfWork.findLastUserBid(this.auctionId, userId);
                }
                
                this.auction = this.mapper.mapAuctionToDetailsDto(auction, this.loggedUserBean.getUserId(), userBid);
                this.bidAmount = this.auction.getMinimalBidAmount();
            }

        }
    }
}

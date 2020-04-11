/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.enterprise.context.SessionScoped;
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
@SessionScoped
public class AuctionDetailsBean implements Serializable {
    
    @Inject
    private LoggedUserBean loggedUserBean;
    private Integer auctionId;
    private final Mapper mapper = new Mapper();
    private String error;
    private AuctionDetailsDto auction;

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
    
    public void cancelAuction() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
            Auction auction = unitOfWork.findAuction(this.auctionId);
            if (auction == null) {
                this.error = "Auction not found!";
                this.auction = null;
            }
            else {
                this.error = null;
                if (auction.canCancel(this.loggedUserBean.getUserId())) {
                    auction.cancel();
                    unitOfWork.saveChanges();
                    this.auction = this.mapper.mapAuctionToDetailsDto(auction, this.loggedUserBean.getUserId());
                }
                else {
                    this.error = "Cannot cancel auction!";
                }
            }
        }
    }
    
    public void bid(BigDecimal amount) {
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
          
            UserProfile user = unitOfWork.findUserById(this.loggedUserBean.getUserId());
            Auction auction = unitOfWork.findAuction(this.auctionId);
            Bid bid = auction.addBid(user, amount);
            //unitOfWork.persist(bid);
            unitOfWork.saveChanges();
            this.auction = this.mapper.mapAuctionToDetailsDto(auction, this.loggedUserBean.getUserId());
        }
    }
    
    private void refresh() {
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
            Auction auction = unitOfWork.findAuction(this.auctionId);
            if (auction == null) {
                this.error = "Auction not found!";
                this.auction = null;
            }
            else {
                this.error = null;
                this.auction = this.mapper.mapAuctionToDetailsDto(auction, this.loggedUserBean.getUserId());
            }
              
        }
    }
}

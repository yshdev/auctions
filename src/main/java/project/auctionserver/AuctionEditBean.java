/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.Auction;
import project.domain.Bid;
import project.domain.UserProfile;
import project.service.AuctionDetailsDto;
import project.service.CategoryDto;
import project.service.Mapper;

/**
 *
 * @author Shalom
 */
@Named
@SessionScoped
public class AuctionEditBean implements Serializable {
    
    @Inject
    private LoggedUserBean loggedUserBean;
    private Integer auctionId;
    private final Mapper mapper = new Mapper();
    private String error;
    private Integer categoryId;
    private String title;
    private String description;
    private Integer startingAmount;
    private Integer winningAmount;
    private Integer reservedPrice;
    private String startingDate;
    private Integer startingHour;
    private int[] hours = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
    private CategoryDto[] categories;
    

    
    @PostConstruct
    public void init() {
        this.updateCategories();
        this.loadAuction();
        //this.setCategoryId(this.getCategories()[0].getId());
    }
    
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
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public Integer getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(Integer startingAmount) {
        this.startingAmount = startingAmount;
    }

    public Integer getWinningAmount() {
        return winningAmount;
    }

    public void setWinningAmount(Integer winningAmount) {
        this.winningAmount = winningAmount;
    }

    public Integer getReservedPrice() {
        return reservedPrice;
    }

    public void setReservedPrice(Integer reservedPrice) {
        this.reservedPrice = reservedPrice;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public Integer getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(Integer startingHour) {
        this.startingHour = startingHour;
    }

    public int[] getHours() {
        return hours;
    }

    public void setHours(int[] hours) {
        this.hours = hours;
    }

    public CategoryDto[] getCategories() {
        return categories;
    }

    public void setCategories(CategoryDto[] categories) {
        this.categories = categories;
    }
    
     
    

    private void updateCategories() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
           this.categories = unitOfWork.getAllCategories();
        }
    }
    
    private void loadAuction() {
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
            Auction auction = unitOfWork.findAuction(this.auctionId);
            if (auction == null) {
                this.error = "Auction not found!";
            }
            else {
                this.error = null;
                this.title = auction.getTitle();
                this.description  = auction.getDescription();
                this.reservedPrice = Integer.parseInt(auction.getReservedPrice().toString());
                this.winningAmount = Integer.parseInt(auction.getWinningAmount().toString());
                this.startingAmount = Integer.parseInt(auction.getStartingAmount().toString());
                this.startingDate = auction.getStartingTime().toLocalDate().toString();
                this.startingHour = auction.getStartingTime().getHour();
            }
        }
    }
}

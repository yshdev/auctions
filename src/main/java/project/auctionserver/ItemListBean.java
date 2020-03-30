package project.auctionserver;

import project.service.CategoryDto;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import project.dal.UnitOfWork;
import project.domain.*;
import project.service.AuctionListItemDto;
import project.service.SortOption;

@Named(value = "itemListBean")
@SessionScoped
public class ItemListBean implements Serializable {
    
    private final String[] sortOptions = {
        "current price - ascending order", "current price - descending order",
        "ending time - ascending order", "ending time - descending order"};
    
    private Integer categoryId;
    private String sortOption;
    private AuctionListItemDto[] activeAuctions;
    private List<Auction> itemsActiveList = new ArrayList<>();
    private List<Auction> bidsActiveList = new ArrayList<>();
    private List<Auction> itemsClosedList = new ArrayList<>();
    private List<Auction> bidsClosedList = new ArrayList<>();
    private Auction chosenAuction;

    public ItemListBean() {
    }
    
    public Integer getCategoryId() {
        return this.categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public void setSortOption(String sortOption) {
        this.sortOption = sortOption;
    }
    
    public void setChosenAuction(Auction chosenAuction) {
        this.chosenAuction = chosenAuction;
    }
    
   
    
    public String getSortOption() {
        return sortOption;
    }
    
    public String[] getSortOptions() {
        return sortOptions;
    }
    
    public AuctionListItemDto[] getActiveAuctions() {
        return this.activeAuctions;
    }
    
    public List<Auction> getItemsActiveList() {
        return itemsActiveList;
    }
    
    public List<Auction> getBidsActiveList() {
        return bidsActiveList;
    }
    
    public List<Auction> getItemsClosedList() {
        return itemsClosedList;
    }
    
    public List<Auction> getBidsClosedList() {
        return bidsClosedList;
    }
    
    public String updateActiveAuctions(Integer userId) {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {

            if (this.getCategoryId() != null) {
                this.activeAuctions = unitOfWork.getActiveAuctions(this.getCategoryId(), SortOption.Current_Price__Ascending, userId);
            }
            else {
                this.activeAuctions = new AuctionListItemDto[0];
            }
        }
        
        return "mainMenu.xhtml";
    }
    
    public Auction getChosenAuction() {
        return chosenAuction;
    }
    
    public CategoryDto[] getCategories() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
           CategoryDto[] categories = unitOfWork.getAllCategories();
           return categories;
        }
    }
    
//    public void createLists(UserProfile connectedUser) {
//        UnitOfWork unitOfWork = UnitOfWork.create();
//        chosenList = unitOfWork.getSortedList(category, sortOption);
//        unitOfWork.close();
//        
//        for (Auction auction : chosenList) {
//            if (auction.getOwner() == connectedUser) {
//                if (auction.isClosed())
//                    itemsClosedList.add(auction);
//                else
//                    itemsActiveList.add(auction);
//            }
//            for (Bid bid : auction.getBids()) {
//                if (bid.getBidder() == connectedUser) {
//                    if (auction.isClosed())
//                        itemsClosedList.add(auction);
//                    else
//                        itemsActiveList.add(auction);
//                }
//            }
//        }
//    }
    
    public String displayItem(Auction auction) {
        this.chosenAuction = auction;
        return "itemDisplay.xhtml";
    }
    
    public String cancelAuction() {
        UnitOfWork unitOfWork = UnitOfWork.create();
        unitOfWork.delete(chosenAuction);
        unitOfWork.saveChanges();
        unitOfWork.close();
        
        return "mainMenu.xhtml";
    }
    
    public BigDecimal minBid() {
        BigDecimal bid = new BigDecimal(chosenAuction.getHighestBid().getAmmount().toString());
        
        long num = chosenAuction.getHighestBid().getAmmount().longValue();
        int digits = (int)(Math.log10(num)+1);
        int diff = 1;
        if (digits > 1)
            diff = 5*(digits-1);
        BigDecimal res = bid.add(new BigDecimal(diff));
        
        return res;
    }
    
    public void newBid(String inputBid, UserProfile connectedUser) {
        
        Bid bid = new Bid(chosenAuction, connectedUser, new BigDecimal(inputBid), new Timestamp(System.currentTimeMillis()));
        UnitOfWork unitOfWork = UnitOfWork.create();
        unitOfWork.persist(bid);
        
        chosenAuction.addBid(connectedUser, new BigDecimal(inputBid));
        
        unitOfWork.saveChanges();
        unitOfWork.close();
    }
}

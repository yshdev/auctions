package project.auctionserver;

import project.service.CategoryDto;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.*;
import project.service.AuctionListItemDto;
import project.service.Mapper;
import project.service.SortOption;

@Named
@ViewScoped
public class AuctionListBean implements Serializable {
    
    @Inject
    private LoggedUserBean loggedUserBean;
    private Mapper mapper = new Mapper();
    
    private final String[] sortOptions = {
        "current price - ascending order", "current price - descending order",
        "ending time - ascending order", "ending time - descending order"};
    
    private Integer categoryId;
    private String sortOption;
    private AuctionListItemDto[] activeAuctions;
    private CategoryDto[] categories;
    private List<Auction> itemsActiveList = new ArrayList<>();
    private List<Auction> bidsActiveList = new ArrayList<>();
    private List<Auction> itemsClosedList = new ArrayList<>();
    private List<Auction> bidsClosedList = new ArrayList<>();
    private Auction chosenAuction;

    public AuctionListBean() {
    }
    
    @PostConstruct
    public void init() {
        this.updateCategories();
        this.setCategoryId(this.getCategories()[0].getId());
        this.updateActiveAuctions();
    }

    public LoggedUserBean getLoggedUserBean() {
        return loggedUserBean;
    }

    public void setLoggedUserBean(LoggedUserBean loggedUserBean) {
        this.loggedUserBean = loggedUserBean;
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
    
    public String updateActiveAuctions() {
        
        Integer userId = this.loggedUserBean == null ? null : this.loggedUserBean.getUserId();
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {

            if (this.getCategoryId() != null) {
                List<Auction> auctions =  unitOfWork.getActiveAuctions(this.getCategoryId(), SortOption.Current_Price__Ascending, userId);
                this.activeAuctions = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
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
        return this.categories;
    }
    
    public void updateCategories() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
           this.categories = unitOfWork.getAllCategories();
        }
    }
    
    
}

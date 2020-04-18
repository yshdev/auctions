package project.auctionserver;

import project.service.CategoryDto;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.*;
import project.service.AuctionListItemDto;
import project.service.Mapper;
import project.service.SortOption;
import project.service.AuctionDetailsDto;
import project.service.BidDto;
import project.service.UserDto;

@Named
@ViewScoped
public class AuctionListBean implements Serializable {
    
    @Inject
    private LoggedUserBean loggedUserBean;
    private Mapper mapper = new Mapper();
    
    private Integer categoryId;
    private SortOption sortOption= SortOption.Current_Price__Ascending;
    private AuctionListItemDto[] activeAuctions;
    private CategoryDto[] categories;
    private AuctionListItemDto[] itemsActiveList;
    private AuctionListItemDto[] bidsActiveList;
    private AuctionListItemDto[] itemsClosedList;
    private AuctionListItemDto[] bidsClosedList;
    private AuctionDetailsDto chosenAuction;
    
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
    
    public void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
    }
    
    public SortOption getSortOption() {
        return sortOption;
    }
    
    public SortOption[] getSortOptions() {
        return SortOption.values();
    }
    
    public AuctionListItemDto[] getActiveAuctions() {
        return this.activeAuctions;
    }
    
    public AuctionListItemDto[] getItemsActiveList() {
        return itemsActiveList;
    }
    
    public AuctionListItemDto[] getBidsActiveList() {
        return bidsActiveList;
    }
    
    public AuctionListItemDto[] getItemsClosedList() {
        return itemsClosedList;
    }
    
    public AuctionListItemDto[] getBidsClosedList() {
        return bidsClosedList;
    }
    
    public void updateActiveAuctions() {
        
        Integer userId = this.loggedUserBean == null ? null : this.loggedUserBean.getUserId();
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {

            if (this.getCategoryId() != null) {
                List<Auction> auctions = unitOfWork.getActiveAuctions(this.getCategoryId(), this.sortOption, userId);
                this.activeAuctions = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
                
                auctions = unitOfWork.getActiveItems(this.getCategoryId(), this.sortOption, userId);
                this.itemsActiveList = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
                
                auctions = unitOfWork.getActiveBids(this.getCategoryId(), this.sortOption, userId);
                this.bidsActiveList = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
                
                auctions = unitOfWork.getClosedItems(this.getCategoryId(), this.sortOption, userId);
                this.itemsClosedList = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
                
                auctions = unitOfWork.getClosedBids(this.getCategoryId(), this.sortOption, userId);
                this.bidsClosedList = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
            }
            else {
                this.activeAuctions = new AuctionListItemDto[0];
            }
        }
    }
    
    public AuctionDetailsDto getChosenAuction() {
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
    
    public String displayItem(Integer auctionId) {
        
        // find auction by auctionId and map to AuctionDetailsDto chosenAuction
        
        
        return "itemDisplay.xhtml";
    }
        
}

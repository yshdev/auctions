package project.auctionserver;

import project.service.CategoryDto;
import java.util.List;
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
import project.service.AuctionDetailsDto;
import project.service.AuctionFilter;

@Named
@ViewScoped
public class PrivateAreaBean implements Serializable {

    @Inject
    private LoggedUserBean loggedUserBean;
    private Mapper mapper = new Mapper();

    private Integer categoryId;
    private SortOption sortOption = SortOption.Current_Price__Ascending;
    private CategoryDto[] categories;
    private AuctionListItemDto[] activeAuctions;
    private AuctionListItemDto[] activeBids;
    private AuctionListItemDto[] closedAuctions;
    private AuctionListItemDto[] closedBids;
    private AuctionDetailsDto chosenAuction;
    private String error;

    public PrivateAreaBean() {
    }

    @PostConstruct
    public void init() {
        this.updateCategories();
        this.setCategoryId(this.getCategories()[0].getId());
        this.updateAuctions();
    }

    public LoggedUserBean getLoggedUserBean() {
        return loggedUserBean;
    }

    public void setLoggedUserBean(LoggedUserBean loggedUserBean) {
        this.loggedUserBean = loggedUserBean;
    }
    
    public CategoryDto[] getCategories() {
        return this.categories;
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
     
    public AuctionListItemDto[] getActiveBids() {
        return activeBids;
    }

    public AuctionListItemDto[] getClosedAuctions() {
        return closedAuctions;
    }

    public AuctionListItemDto[] getClosedBids() {
        return closedBids;
    }
    
    

    public String getError() {
        return error;
    }

    public void updateAuctions() {

        if (!this.assertUserIsLoggedIn()) {
            return;
        }

        Integer userId = this.loggedUserBean.getUserId();

        try (UnitOfWork unitOfWork = UnitOfWork.create()) {

            List<Auction> auctions = unitOfWork.getUserAuctions(this.getCategoryId(), this.sortOption, userId, AuctionFilter.ALL);
            
            this.activeAuctions = auctions.stream()
                    .filter(a -> !a.isClosed())
                    .map(a -> this.mapper.mapAuctionToListItemDto(a, userId))
                    .toArray(AuctionListItemDto[]::new);
            
            this.closedAuctions = auctions.stream()
                    .filter(a -> a.isClosed())
                    .map(a -> this.mapper.mapAuctionToListItemDto(a, userId))
                    .toArray(AuctionListItemDto[]::new);

            auctions = unitOfWork.getUserActiveBids(this.getCategoryId(), this.sortOption, userId);
            this.activeBids = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);

            auctions = unitOfWork.getClosedBids(this.getCategoryId(), this.sortOption, userId);
            this.closedBids = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
        }
    }
 

    

    public void updateCategories() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
            this.categories = unitOfWork.getAllCategories();
        }
    }

    private boolean assertUserIsLoggedIn() {
        if (this.loggedUserBean == null || this.loggedUserBean.getUserId() == null) {
            this.error = "You must be logged in to view this page.";
            return false;
        }

        return true;
    }

}

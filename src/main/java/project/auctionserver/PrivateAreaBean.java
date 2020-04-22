package project.auctionserver;

import project.service.CategoryDto;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.*;
import project.service.AuctionBidsTuple;
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
    private List<AuctionListItemDto> activeAuctions;
    private List<AuctionListItemDto> activeBids;
    private List<AuctionListItemDto> closedAuctions;
    private List<AuctionListItemDto> closedBids;
    
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

    public List<AuctionListItemDto>getActiveAuctions() {
        return this.activeAuctions;
    }
     
    public List<AuctionListItemDto> getActiveBids() {
        return activeBids;
    }

    public List<AuctionListItemDto> getClosedAuctions() {
        return closedAuctions;
    }

    public List<AuctionListItemDto>getClosedBids() {
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

            List<AuctionBidsTuple> auctions = unitOfWork.getUserAuctions(this.getCategoryId(), this.sortOption, userId);
            
            this.activeAuctions = auctions.stream()
                    .filter(a -> !a.getAuction().isClosed())
                    .map(a -> this.mapper.mapAuctionToListItemDto(a.getAuction(), userId, a.getUserBid()))
                    .collect(Collectors.toList());
            
            this.closedAuctions = auctions.stream()
                    .filter(a -> a.getAuction().isClosed())
                    .map(a -> this.mapper.mapAuctionToListItemDto(a.getAuction(), userId, a.getUserBid()))
                    .collect(Collectors.toList());
                    

            //auctions = unitOfWork.getUserBids(this.getCategoryId(), this.sortOption, userId, AuctionFilter.ACTIVE);
            List<AuctionBidsTuple> bids = unitOfWork.getUserBids(this.getCategoryId(), this.sortOption, userId);
            this.activeBids = bids.stream()
                    .filter(a -> !a.getAuction().isClosed())
                    .map(b -> this.mapper.mapAuctionToListItemDto(b.getAuction(), userId, b.getUserBid()))
                    .collect(Collectors.toList());
            
            this.closedBids = bids.stream()
                    .filter(a -> a.getAuction().isClosed())
                    .map(b -> this.mapper.mapAuctionToListItemDto(b.getAuction(), userId, b.getUserBid()))
                    .collect(Collectors.toList());
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

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
    
    public void updateActiveAuctions() {
        
        Integer userId = this.loggedUserBean == null ? null : this.loggedUserBean.getUserId();
        
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {

            if (this.getCategoryId() != null) {
                List<Auction> auctions = unitOfWork.getActiveAuctions(this.getCategoryId(), this.sortOption);
                this.activeAuctions = auctions.stream().map(a -> this.mapper.mapAuctionToListItemDto(a, userId)).toArray(AuctionListItemDto[]::new);
            }
            else {
                this.activeAuctions = new AuctionListItemDto[0];
            }
        }
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

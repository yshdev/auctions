package project.auctionserver;

import project.service.CategoryDto;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import project.dal.UnitOfWork;
import project.domain.Category;
import project.domain.Auction;

@Named(value = "itemListBean")
@SessionScoped
public class ItemListBean implements Serializable {
    
    private final String[] sortOptions = {
        "current price - ascending order", "current price - descending order",
        "ending time - ascending order", "ending time - descending order"};
    
    private Category category;
    private String sortOption;
    private List<Auction> chosenList;

    public ItemListBean() {
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public void setSortOption(String sortOption) {
        this.sortOption = sortOption;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public String getSortOption() {
        return sortOption;
    }
    
    public String[] getSortOptions() {
        return sortOptions;
    }
    
    public List<Auction> getChosenList() {
        return chosenList;
    }
    
    public CategoryDto[] getCategories() {
        try (UnitOfWork unitOfWork = UnitOfWork.create()) {
           CategoryDto[] categories = unitOfWork.getAllCategories();
           return categories;
        }
    }
    
    public void createList() {
        UnitOfWork unitOfWork = UnitOfWork.create();
        chosenList = unitOfWork.getSortedList(category, sortOption);
        unitOfWork.close();
    }
}

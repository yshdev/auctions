package project.auctionserver;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import project.domain.Auction;
import project.domain.Category;
import project.domain.UserProfile;
import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import project.dal.UnitOfWork;

@Named(value = "newItem")
@RequestScoped
public class newItem {

    private Category category;
    private String subCategory;
    private String title;
    private String description;
    private BigDecimal startingAmount;
    private LocalDate startDate;
    private int startingHour;
    private int days;
    private BigDecimal winningBid;
    private String errMessage;
    
    public newItem() {
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setStartingBid(int startingBid) {
        this.startingAmount = new BigDecimal(startingBid);
    }
    
    public void setStartTime(String startTime) {
         
        this.startDate = LocalDate.parse(startTime);
         
    }
    
    public void setDays(int days) {
        this.days = days;
    }
    
    public void setWinningBid(int winningBid) {
        this.winningBid = new BigDecimal(winningBid);
    }
    
    public Category getCategory() {
        return category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getStartingBid() {
        return startingAmount;
    }
    
    public LocalDate getStartTime() {
        return startDate;
    }
    
    public int getDays() {
        return days;
    }
    
    public BigDecimal getWinningBid() {
        return winningBid;
    }
    
    public String getErrMessage() {
        return errMessage;
    }
    
    public String addItem(UserProfile currentUser) {
        Auction auction = new Auction(currentUser, category, this.title, this.startDate, this.startingHour, this.days, this.startingAmount, null, null);
        
        
        UnitOfWork unitOfWork = UnitOfWork.create();
        unitOfWork.persist(auction);
        unitOfWork.saveChanges();
        unitOfWork.close();
        
        return "private.xhtml";
    }
}

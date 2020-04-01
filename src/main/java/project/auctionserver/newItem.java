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
import project.dal.UnitOfWork;

@Named(value = "newItem")
@RequestScoped
public class newItem {

    private Category category;
    private String subCategory;
    private String title;
    private String description;
    private BigDecimal startingBid;
    private Date startTime;
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
        this.startingBid = new BigDecimal(startingBid);
    }
    
    public void setStartTime(String startTime) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try{
            this.startTime = format.parse(startTime);
        }
        catch (ParseException e) {
            
        }
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
        return startingBid;
    }
    
    public Date getStartTime() {
        return startTime;
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
        Auction auction = new Auction(currentUser, category);
        
        
        UnitOfWork unitOfWork = UnitOfWork.create();
        unitOfWork.persist(auction);
        unitOfWork.saveChanges();
        unitOfWork.close();
        
        return "private.xhtml";
    }
}

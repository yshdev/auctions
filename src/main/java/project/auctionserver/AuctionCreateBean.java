/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import project.dal.ImageUtils;
import project.dal.UnitOfWork;
import project.domain.Auction;
import project.domain.Category;
import project.domain.Settings;
import project.domain.UserProfile;
import project.service.CategoryDto;
import project.service.Mapper;

/**
 *
 * @author Shalom
 */
@Named
@ViewScoped
public class AuctionCreateBean implements Serializable {

    @Inject
    private LoggedUserBean loggedUserBean;
    private Integer auctionId;
    private final Mapper mapper = new Mapper();
    private String error;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal startingAmount = new BigDecimal(100);
    private BigDecimal winningAmount;
    private BigDecimal reservedPrice;
    private String startingDateText;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private LocalDate minOpeningDate;
    private int numOfDays = 1;
    private CategoryDto[] categories;
    private UploadedFile imageFile;

    public AuctionCreateBean() {
    }

    @PostConstruct
    public void init() {
        this.updateCategories();

        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() > Settings.OPENING_HOUR - 1) {
            this.minOpeningDate = now.plusDays(1).toLocalDate();
        } else {
            this.minOpeningDate = now.toLocalDate();
        }

        this.openingTime = this.minOpeningDate.atTime(Settings.OPENING_HOUR, 0);
        this.closingTime = this.openingTime.plusDays(this.numOfDays);
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

    public BigDecimal getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(BigDecimal startingAmount) {
        this.startingAmount = startingAmount;
        this.updateAmounts();
    }

    public BigDecimal getWinningAmount() {
        return winningAmount;
    }

    public void setWinningAmount(BigDecimal winningAmount) {
        this.winningAmount = winningAmount;
    }

    public BigDecimal getReservedPrice() {
        return reservedPrice;
    }

    public void setReservedPrice(BigDecimal reservedPrice) {
        this.reservedPrice = reservedPrice;
        this.updateAmounts();
    }

    public BigDecimal getMinReservedPrice() {
        return this.startingAmount;
    }

    public BigDecimal getMinWinningAmount() {
        return this.reservedPrice != null ? this.reservedPrice : this.startingAmount;
    }

    public int getNumOfDays() {
        return this.numOfDays;
    }

    public void setNumOfDays(int numOfDays) {
        this.numOfDays = numOfDays;
        this.closingTime = this.openingTime.plusDays(this.numOfDays);
    }

    public LocalDate getOpeningDate() {
        return openingTime.toLocalDate();
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingTime = openingDate.atTime(Settings.OPENING_HOUR, 0);
        this.closingTime = this.openingTime.plusDays(this.numOfDays);
    }

    public String getOpeningTime() {
        return this.openingTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public String getClosingTime() {
        return this.closingTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public CategoryDto[] getCategories() {
        return categories;
    }

    public void setCategories(CategoryDto[] categories) {
        this.categories = categories;
    }

    public String hourToString(int hour) {
        return ((Integer) hour).toString() + ":00";
    }

    public LocalDate getMinOpeningDate() {
        return this.minOpeningDate;
    }

    public UploadedFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(UploadedFile imageFile) {
        this.imageFile = imageFile;
    }

    public void handleImageUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();

    }

    public void saveAuction() {

        if (this.loggedUserBean.getUserId() == null) {
            this.showError("Only logged in users can create auctions. Log in or register and then try again.");
        } else {

            try ( UnitOfWork unitOfWork = UnitOfWork.create()) {

                UserProfile user = unitOfWork.findUserById(this.loggedUserBean.getUserId());
                Category category = unitOfWork.findCategroyById(this.getCategoryId());

                Auction auction = new Auction(user, category, this.getTitle(), this.openingTime.toLocalDate(), Settings.OPENING_HOUR, this.numOfDays, this.getStartingAmount(),
                        this.getWinningAmount(), this.getReservedPrice());
                
                auction.setDescription(description);
                if (this.imageFile != null)
                    auction.setPicture(this.imageFile.getContent());
                
                unitOfWork.persist(auction);
                unitOfWork.saveChanges();
                
            }

            if (this.error == null) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("private.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(AuctionCreateBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void updateAmounts() {
        if (this.reservedPrice != null && this.reservedPrice.compareTo(this.startingAmount) < 0) {
            this.reservedPrice = this.startingAmount;
        }
        if (this.winningAmount != null) {

            if (this.reservedPrice != null) {
                if (this.winningAmount.compareTo(this.reservedPrice) < 0) {
                    this.winningAmount = this.reservedPrice;
                }
            } else {
                if (this.winningAmount.compareTo(this.startingAmount) < 0) {
                    this.winningAmount = this.startingAmount;
                }
            }
        }
    }

    private void updateCategories() {
        try ( UnitOfWork unitOfWork = UnitOfWork.create()) {
            this.categories = unitOfWork.getAllCategories();
        }
    }

    private void showError(String error) {
        this.error = error;
    }

    private void clearError() {
        this.error = null;
    }

}

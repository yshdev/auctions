/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import project.dal.UnitOfWork;
import project.domain.Auction;

/**
 *
 * @author Shalom
 */
@Named
@ViewScoped
public class AuctionCancelBean implements Serializable {

    @Inject
    private LoggedUserBean loggedUserBean;
    private Integer auctionId;
    private String error;
    private String title;
    private boolean canCancel;
    private boolean isLoaded;

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
        if (auctionId != null && !auctionId.equals(this.auctionId)) {
            this.auctionId = auctionId;
            this.loadAuction();
        }
    }

    public boolean getIsLoaded() {
        return this.isLoaded;
    }

    public String getTitle() {
        return this.title;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean getCanCancel() {
        return this.canCancel;
    }

    public void cancel() {

        this.clearError();

        // Create new auction
        if (this.auctionId == null) {
            this.showError("No auction selected.");
        } else if (this.loggedUserBean.getUserId() == null) {
            this.showError("Only logged in users can create auctions. Log in or register and then try again.");
        } else if (!this.getCanCancel()) {
            this.showError("Cannot cancel auction.");
        } else {
            try ( UnitOfWork unitOfWork = UnitOfWork.create()) {

                Auction auction = unitOfWork.findAuction(this.auctionId);
                auction.cancel();
                unitOfWork.persist(auction);
                unitOfWork.saveChanges();
            }
        }

        if (this.error == null) {
            this.redirectToPrivateArea();
        }
    }

    public void abort() {
        this.redirectToPrivateArea();
    }

    private void loadAuction() {

        this.isLoaded = false;

        if (this.loggedUserBean.getUserId() == null) {
            this.showError("Only logged in users can create auctions. Log in or register and then try again.");
        } else {

            try ( UnitOfWork unitOfWork = UnitOfWork.create()) {

                if (this.auctionId == null) {
                    this.showError("No auction selected or not found.");
                } else {
                    Auction auction = unitOfWork.findAuction(this.auctionId);
                    if (auction == null) {
                        this.error = "Auction not found!";
                    } else {
                        this.canCancel = auction.canCancel(this.loggedUserBean.getUserId());
                        if (!this.canCancel) {
                            this.showError("Cannot cancel auction.");
                        } else {
                            this.error = null;
                            this.title = auction.getTitle();
                            this.canCancel = true;
                        }
                    }
                }
           }
        }
    }

    private void checkCanCancel() {

        if (this.auctionId == null) {
            this.showError("No auction selected or not found.");
        } else if (this.loggedUserBean.getUserId() == null) {
            this.showError("Only logged in users can create auctions. Log in or register and then try again.");
        } else if (!this.getCanCancel()) {
            this.showError("Cannot cancel auction.");
        }
    }

    private void showError(String error) {
        this.error = error;
    }

    private void clearError() {
        this.error = null;
    }

    private void redirectToPrivateArea() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("private.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(AuctionCancelBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

/**
 *
 * @author Shalom
 */
public enum AuctionStatus {
    NOT_OPENNED_YET("Not Open Yet"),
    OPEN("Open"),
    CLOSING("Closing"),
    CLOSED("Closed"),
    CANCELED("Canceled");
    
    private final String statusName;
    private AuctionStatus(String statusName) {
        this.statusName = statusName;
    }
    
    @Override
    public String toString(){
        return this.statusName;
    }
}
 
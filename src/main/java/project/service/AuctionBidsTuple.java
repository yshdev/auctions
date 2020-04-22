/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import project.domain.Auction;
import project.domain.Bid;

/**
 *
 * @author Shalom
 */
public class AuctionBidsTuple {
    
    private final Auction auction;
    private final Bid userBid;
    private final Bid highestBid;

    public AuctionBidsTuple(Auction auction, Bid highestBid, Bid userBid) {
        this.auction = auction;
        this.userBid = userBid;
        this.highestBid = highestBid;
    }

    public Auction getAuction() {
        return auction;
    }

    public Bid getUserBid() {
        return userBid;
    }

    public Bid getHighestBid() {
        return highestBid;
    }
    
    
}

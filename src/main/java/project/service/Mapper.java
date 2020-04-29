/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import project.domain.Auction;
import project.domain.Bid;
import project.domain.Category;
import project.domain.UserProfile;

/**
 *
 * @author Shalom
 */
public class Mapper {

    public UserDto mapUserToDto(UserProfile user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setId(user.getId());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    public AuctionDetailsDto mapAuctionToDetailsDto(Auction auction, Integer userId, Bid userBid) {

        AuctionDetailsDto details = new AuctionDetailsDto();
        
        this.fillAuctionDto(auction, userId, userBid, details);
        
        details.setClosingTime(auction.getClosingTime());
        details.setDescription(auction.getDescription());
        details.setHighestBid(this.mapBidToDto(auction.getHighestBid()));
        if (auction.getWinningBid() != null) {
            details.setWinningBid(this.mapBidToDto(auction.getWinningBid()));
        }
        details.setNumberOfBids(auction.getBids().size());
        details.setOwner(this.mapUserToDto(auction.getOwner()));
        details.setStartingTime(auction.getStartingTime());
        details.setActualClosingTime(auction.getActualClosingTime());
        details.setMinimalBidAmount(auction.getMinimalBidAmount());
        details.setWinningAmount(auction.getWinningAmount());
        details.setReservedPrice(auction.getReservedPrice());
        

        return details;
    }

    public AuctionListItemDto mapAuctionToListItemDto(Auction auction, Integer userId, Bid userBid) {
        AuctionListItemDto dto = new AuctionListItemDto();
        this.fillAuctionDto(auction, userId, userBid, dto);
        return dto;
    }

    public CategoryDto mapCategoryToDto(Category category) {
        CategoryDto dto = new CategoryDto(category.getId(), category.getTitle());
        return dto;
    }

    public BidDto mapBidToDto(Bid bid) {

        if (bid == null) {
            return null;
        }

        BidDto dto = new BidDto();
        dto.setId(bid.getId());
        dto.setBidder(this.mapUserToDto(bid.getBidder()));
        dto.setTimestamp(bid.getTimestamp());
        dto.setAmount(bid.getAmount());

        return dto;
    }
    
    private void fillAuctionDto(Auction auction, Integer userId, Bid userBid, AuctionListItemDto dto) {
        dto.setId(auction.getId());
        dto.setCategory(this.mapCategoryToDto(auction.getCategory()));
        dto.setTitle(auction.getTitle());
        dto.setStatus(auction.getStatus());
        dto.setStartingAmount(auction.getStartingAmount());
        dto.setImageBytes(auction.getImageBytes());
        dto.setUserIsWinner(auction.getWinningBid() != null && userId != null && auction.getWinningBid().getBidder().getId() == userId);
        dto.setUserIsNotWinner(!auction.isClosed() || auction.isCanceled() || auction.getWinningBid() == null || 
                auction.getWinningBid().getBidder().getId() != userId);
        dto.setUserIsOwner(userId != null && auction.getOwner().getId() == userId);
        
        if (auction.getHighestBid() != null) {
            dto.setLatestBidAmount(auction.getHighestBid().getAmount());
        }
        if (userId == null) {
            dto.setCanCancel(false);
            dto.setCanEdit(false);
            dto.setCanBid(false);
        } else {
            dto.setCanCancel(auction.canCancel(userId));
            dto.setCanEdit(auction.canEdit(userId));
            dto.setCanBid(auction.canBid(userId));
        }
        if (userBid != null) {
            dto.setUserBidAmount(userBid.getAmount());
            dto.setUserBidTimestamp(userBid.getTimestamp());
        }
    }
}

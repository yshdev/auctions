/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Shalom
 */
public class AuctionDtoForOwner {
    private int id;
    private String title;
    private String description;
    private BigDecimal startingAmount;
    private Date startingTime;
    private Date endingTime;
    private int numberOfBids;
    private boolean isClosed;
    private CategoryDto category;
    private BigDecimal highestBidAmount;
    private UserDto owner;
    private BigDecimal winningBidAmount;
    
    
}


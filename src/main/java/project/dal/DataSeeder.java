/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Date;
import project.domain.Auction;
import project.domain.Category;
import project.domain.UserProfile;

/**
 *
 * @author Shalom
 */
public class DataSeeder {
    
    public static void Seed() {
        UnitOfWork unitOfWork = UnitOfWork.create();
        if (unitOfWork.isEmpty()) {
            
            Category israeliCoins = new Category("Israeli Coins");
            UserProfile yaniv = new UserProfile();
            
            Auction israeliCoinsAuction = new Auction(yaniv, "100 Isralei ancient coins", "100 Israeli ancient coins from 100 BC", 
                    israeliCoins,  new Date(), 12, new BigDecimal(10000.0), new BigDecimal(20000.0), new BigDecimal(15000.0));
            unitOfWork.persist(israeliCoins);
            unitOfWork.saveChanges();
            unitOfWork.close();
        }
        
    }
}

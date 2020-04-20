/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import project.domain.*;
import project.service.*;

/**
 *
 * @author Shalom
 */
public class UnitOfWork implements AutoCloseable {

    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final EntityTransaction transaction;
    private static boolean _isInitialized = false;

    private UnitOfWork(EntityManagerFactory emf, EntityManager em, EntityTransaction transaction) {
        this.emf = emf;
        this.em = em;
        this.transaction = transaction;
    }

    public static UnitOfWork create() {

        seed();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("auctions_persistence_unit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        return new UnitOfWork(emf, em, tr);
    }

    public CategoryDto[] getAllCategories() {
        TypedQuery<Category> query = this.em.createQuery("Select c from Category c", Category.class);
        Stream<Category> categories = query.getResultStream();
        CategoryDto[] dtos = categories
                .map(c -> new CategoryDto(c.getId(), c.getTitle()))
                .toArray(CategoryDto[]::new);

        return dtos;
    }

    public void deleteAuction(Auction chosenAuction) {
        em.remove(chosenAuction);
    }

    public boolean isUniqueUsername(String username) {
        try {
            Long count
                    = this.em.createQuery("SELECT COUNT(u) FROM UserProfile u WHERE u.username = :inputUserName", Long.class)
                            .setParameter("inputUserName", username)
                            .getSingleResult();
            return count == 0;
        } catch (NoResultException e) {
        }
        return false;
    }

    public UserProfile findUserByUsername(String userName) {

        UserProfile user = null;
        try {
            user  = (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :inputName")
                .setParameter("inputName", userName)
                .getSingleResult();
            return user;
            
        } catch (NoResultException e) { // no such UserName in DB
            return null;
        }
    }
    
    public UserProfile findUserById(int userId) {
        UserProfile user = null;
        try {
            user  = (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.id = :userId")
                .setParameter("userId", userId)
                .getSingleResult();
            return user;
            
        } catch (NoResultException e) { // no such UserName in DB
            return null;
        }
    }

    public List<Auction> getActiveAuctions(int categoryId, SortOption sortOption) {

        String s = "SELECT a FROM Auction a LEFT JOIN FETCH a.highestBid b WHERE  a.category.id = :categoryId AND a.actualClosingTime IS NULL AND a.startingTime < :now AND a.closingTime > :now";
        s = this.addSortToAuctionsQuery(s, sortOption);
        
        TypedQuery<Auction> query = this.em.createQuery(s, Auction.class)
                .setParameter("categoryId", categoryId)
                .setParameter("now", LocalDateTime.now());

        List<Auction> auctions = query.getResultList();
        
        return auctions;
    }
    
    public List<Auction> getUserActiveAuctions(int categoryId, SortOption sortOption, int userId) {

        String s = "SELECT a FROM Auction a LEFT JOIN FETCH a.highestBid b WHERE a.category.id = :categoryId AND a.actualClosingTime IS NULL AND a.startingTime < :now AND a.closingTime > :now AND a.owner.id = :userId";
        s = this.addSortToAuctionsQuery(s, sortOption);
        
        TypedQuery<Auction> query = this.em.createQuery(s, Auction.class)
                .setParameter("categoryId", categoryId)
                .setParameter("now", LocalDateTime.now())
                .setParameter("userId", userId);

        List<Auction> auctions = query.getResultList();
        
        return auctions;
    }
    
    public List<Auction> getActiveBids(int categoryId, SortOption sortOption, Integer userId) {

        String s = "SELECT a FROM Auction a WHERE a.category.id = :categoryId AND a.actualClosingTime IS NULL AND a.startingTime < :now AND a.closingTime > :now AND userId =  ANY(a.bids)";
        
        switch (sortOption) {
            case Current_Price__Ascending:
                s += " ORDER BY a.highestBid";
                break;
                
            case Current_Price__Descending:
                s += " ORDER BY a.highestBid DESC";
                break;
                
            case Ending_Time__Asecnding:
                s += " ORDER BY a.endingTime";
                break;

            case Ending_Time__Descending:
            default:
                s += " ORDER BY a.endingTime DESC";
                break;
        }
        
        TypedQuery<Auction> query;
        
        query = this.em.createQuery(s, Auction.class);
        
        query = query.setParameter("categoryId", categoryId);
        query = query.setParameter("now", LocalDateTime.now());

        List<Auction> auctions = query.getResultList();
        
        return auctions;
    }
    
    public List<Auction> getClosedItems(int categoryId, SortOption sortOption, Integer userId) {

        String s = "SELECT a FROM Auction a WHERE a.category.id = :categoryId AND (a.actualClosingTime IS NOT NULL OR a.startingTime > :now OR a.closingTime < :now) AND a.owner.id = userId";
        
        switch (sortOption) {
            case Current_Price__Ascending:
                s += " ORDER BY a.highestBid";
                break;
                
            case Current_Price__Descending:
                s += " ORDER BY a.highestBid DESC";
                break;
                
            case Ending_Time__Asecnding:
                s += " ORDER BY a.endingTime";
                break;

            case Ending_Time__Descending:
            default:
                s += " ORDER BY a.endingTime DESC";
                break;
        }
        
        TypedQuery<Auction> query;
        
        query = this.em.createQuery(s, Auction.class);
        
        query = query.setParameter("categoryId", categoryId);
        query = query.setParameter("now", LocalDateTime.now());

        List<Auction> auctions = query.getResultList();
        
        return auctions;
    }
    
    public List<Auction> getClosedBids(int categoryId, SortOption sortOption, Integer userId) {

        String s = "SELECT a FROM Auction a WHERE a.category.id = :categoryId AND (a.actualClosingTime IS NOT NULL OR a.startingTime > :now OR a.closingTime < :now) AND userId =  ANY(a.bids)";
        
        switch (sortOption) {
            case Current_Price__Ascending:
                s += " ORDER BY a.highestBid";
                break;
                
            case Current_Price__Descending:
                s += " ORDER BY a.highestBid DESC";
                break;
                
            case Ending_Time__Asecnding:
                s += " ORDER BY a.endingTime";
                break;

            case Ending_Time__Descending:
            default:
                s += " ORDER BY a.endingTime DESC";
                break;
        }
        
        TypedQuery<Auction> query;
        
        query = this.em.createQuery(s, Auction.class);
        
        query = query.setParameter("categoryId", categoryId);
        query = query.setParameter("now", LocalDateTime.now());

        List<Auction> auctions = query.getResultList();
        
        return auctions;
    }
    
    
    public Auction findAuction(int auctionId) {
        
        TypedQuery<Auction> query = this.em.createQuery("SELECT a FROM Auction a LEFT JOIN FETCH a.highestBid b WHERE a.id = :auctionId", Auction.class)
                .setMaxResults(1)
                .setParameter("auctionId", auctionId);
        
        List<Auction> auctions = query.getResultList();
        if (auctions.size() == 1) {
            return auctions.get(0);
        }
        return null;
    }
    
    public Bid findLastUserBid(int auctionId, int userId) {
        
        TypedQuery<Bid> query = this.em.createQuery("SELECT b FROM Bid b WHERE b.auction.id = :auctionId and b.bidder.id = :userId ORDER BY b.amount DESC", Bid.class)
                .setMaxResults(1)
                .setParameter("auctionId", auctionId)
                .setParameter("userId", userId);
        
        List<Bid> bids = query.getResultList();
        if (bids.size() == 1) {
            return bids.get(0);
        }
        return null;
    }
    
    public Category findCategroyById(int categoryId) {
        
        TypedQuery<Category> query = this.em.createQuery("SELECT c FROM Category c WHERE c.id = :categoryId", Category.class)
                .setMaxResults(1)
                .setParameter("categoryId", categoryId);
        
        List<Category> categories = query.getResultList();
        if (categories.size() == 1) {
            return categories.get(0);
        }
        return null;
    }
     

    public boolean isEmpty() {
        TypedQuery<Integer> query = this.em.createQuery("Select count(c) from Category c", Integer.class);
        return query.getFirstResult() == 0;
    }

    public void saveChanges() {
        this.transaction.commit();
    }

    
    @Override
    public void close() {
        this.em.close();
        this.emf.close();
    }

    public void persist(Object obj) {
        this.em.persist(obj);
    }
    
    

    private static synchronized void seed() {

        if (!_isInitialized) {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("auctions_persistence_unit");
            EntityManager emanager = emf.createEntityManager();

            long categoriesCount = emanager.createQuery("Select count(c) from Category c", Long.class).getSingleResult();

            if (categoriesCount == 0) {

                try {
                    emanager.getTransaction().begin();

                    HashAndSaltPair hashAndSalt = Security.hash("shalom");
                    UserProfile yaniv = new UserProfile("yaniv", "Yaniv", "Shalom", "yaniv@gmail.com", "054-56534432", hashAndSalt.getHash(), hashAndSalt.getSalt());
                    emanager.persist(yaniv);

                    hashAndSalt = Security.hash("gross");
                    UserProfile aharon = new UserProfile("aharon", "Aharon", "Gross", "aharon@gmail.com", "054-56534434", hashAndSalt.getHash(), hashAndSalt.getSalt());
                    emanager.persist(aharon);

                    Category israeliCoins = new Category("Israeli Coins");
                    emanager.persist(israeliCoins);

                    Category israeliArt = new Category("Israeli Art");
                    emanager.persist(israeliArt);

                    Auction a1 = new Auction(yaniv, israeliCoins, "Israeli 100 Pruta coin", LocalDate.now(), 18, 12, new BigDecimal(150.0), new BigDecimal(300.0), new BigDecimal(250.0));
                    a1.setDescription("Israeli 100 Pruta from 1954 (Not magnetic)");
                    a1.setPicture(ImageUtils.loadImage("Israeli100Prutacoin.jpg"), "jpg");
                    emanager.persist(a1);
                    emanager.flush();

                    Auction a2 = new Auction(aharon, israeliCoins, "Israeli Silver Coins", LocalDate.now(), 18, 10, new BigDecimal(1000.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a2.setDescription("Israeli Govenment silver coins");
                    a2.setPicture(ImageUtils.loadImage("IsraeliSilverCoins.jpg"), "jpg");
                    emanager.persist(a2);
                    emanager.flush();

                    Auction a3 = new Auction(yaniv, israeliArt, "Sheep Head - Menashe Kadishman", LocalDate.now(), 18, 20, new BigDecimal(800.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a3.setDescription("Sheep head 60x50 - Acrylic on canvas - h:60 w:50 cm - signed lower center and again on the reverse");
                    a3.setPicture(ImageUtils.loadImage("SheepHead-MenasheKadishman.jpg"), "jpg");
                    emanager.persist(a3);
                    emanager.flush();

                    emanager.getTransaction().commit();

                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(UnitOfWork.class.getName()).log(Level.SEVERE, null, ex);
                }

                emanager.close();
                emf.close();

                
            }
            _isInitialized = true;
        }
    }
    
     private String addSortToAuctionsQuery(String query, SortOption sortOption) {
        switch (sortOption) {
            case Current_Price__Ascending:
                return query + " ORDER BY COALESCE(b.amount, a.startingAmount) ASC";
                
            case Current_Price__Descending:
                return query + " ORDER BY COALESCE(b.amount, a.startingAmount) DESC";
                
            case Ending_Time__Asecnding:
                return query + " ORDER BY a.closingTime ASC";

            case Ending_Time__Descending:
            default:
                return query + " ORDER BY a.closingTime DESC";
        }
    }
}

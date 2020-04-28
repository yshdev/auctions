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
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    private final Mapper mapper = new Mapper();

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

    public List<CategoryDto> getAllCategories() {
        TypedQuery<Category> query = this.em.createQuery("Select c from Category c", Category.class);
        Stream<Category> categories = query.getResultStream();
        List<CategoryDto> dtos = categories
                .map(c -> new CategoryDto(c.getId(), c.getTitle()))
                .collect(Collectors.toList());

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
            user = (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :inputName")
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
            user = (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.id = :userId")
                    .setParameter("userId", userId)
                    .getSingleResult();
            return user;

        } catch (NoResultException e) { // no such UserName in DB
            return null;
        }
    }

    public List<AuctionBidsTuple> getActiveAuctions(Integer categoryId, Integer userId, SortOption sortOption) {

        if (categoryId != null) {
            String s = "SELECT a, hb, ub FROM Auction a LEFT JOIN a.highestBid hb LEFT JOIN Bid ub"
                    + " ON ub.auction = a  AND ub.bidder.id = :userId AND ub.isUserHighest = 1"
                    + " WHERE a.category.id = :categoryId AND a.actualClosingTime IS NULL AND a.startingTime < :now AND a.closingTime > :now";
            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Object[]> query = this.em.createQuery(s, Object[].class)
                    .setParameter("categoryId", categoryId)
                    .setParameter("userId", userId)
                    .setParameter("now", LocalDateTime.now());

            List<Object[]> values = query.getResultList();
            List<AuctionBidsTuple> result = values.stream()
                    .map(v -> new AuctionBidsTuple((Auction) v[0], (Bid) v[1], (Bid) v[2]))
                    .collect(Collectors.toList());

            return result;

        } else {

            String s = "SELECT a, hb, ub FROM Auction a LEFT JOIN a.highestBid hb LEFT JOIN Bid ub"
                    + " ON ub.auction = a  AND ub.bidder.id = :userId AND ub.isUserHighest = 1"
                    + " WHERE a.actualClosingTime IS NULL AND a.startingTime < :now AND a.closingTime > :now";
            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Object[]> query = this.em.createQuery(s, Object[].class)
                    .setParameter("userId", userId)
                    .setParameter("now", LocalDateTime.now());

            List<Object[]> values = query.getResultList();
            List<AuctionBidsTuple> result = values.stream()
                    .map(v -> new AuctionBidsTuple((Auction) v[0], (Bid) v[1], (Bid) v[2]))
                    .collect(Collectors.toList());

            return result;
        }
    }

    public List<AuctionBidsTuple> getUserAuctions(Integer categoryId, SortOption sortOption, int userId) {

        if (categoryId != null) {
            String s = "SELECT a FROM Auction a LEFT JOIN FETCH a.highestBid hb"
                    + " WHERE a.category.id = :categoryId"
                    + " AND a.owner.id = :userId";

            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Auction> query = this.em.createQuery(s, Auction.class)
                    .setParameter("categoryId", categoryId)
                    .setParameter("userId", userId);

            List<Auction> auctions = query.getResultList();
            List<AuctionBidsTuple> result = auctions.stream()
                    .map(a -> new AuctionBidsTuple(a, a.getHighestBid(), null))
                    .collect(Collectors.toList());

            return result;
        } else {
            String s = "SELECT a FROM Auction a LEFT JOIN FETCH a.highestBid hb"
                    + " WHERE a.owner.id = :userId";

            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Auction> query = this.em.createQuery(s, Auction.class)
                    .setParameter("userId", userId);

            List<Auction> auctions = query.getResultList();
            List<AuctionBidsTuple> result = auctions.stream()
                    .map(a -> new AuctionBidsTuple(a, a.getHighestBid(), null))
                    .collect(Collectors.toList());

            return result;
        }
    }

    public List<AuctionBidsTuple> getUserBids(Integer categoryId, SortOption sortOption, int userId) {

        if (categoryId != null) {

            String s = "SELECT a, hb, ub FROM Bid ub INNER JOIN FETCH ub.auction a LEFT JOIN FETCH a.highestBid hb"
                    + " WHERE ub.isUserHighest = 1"
                    + " AND a.category.id = :categoryId"
                    + " AND ub.bidder.id = :userId";

            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Object[]> query = this.em.createQuery(s, Object[].class)
                    .setParameter("categoryId", categoryId)
                    .setParameter("userId", userId);

            List<Object[]> values = query.getResultList();
            List<AuctionBidsTuple> result = values.stream()
                    .map(v -> new AuctionBidsTuple((Auction) v[0], (Bid) v[1], (Bid) v[2]))
                    .collect(Collectors.toList());

            return result;
        } else {
            String s = "SELECT a, hb, ub FROM Bid ub INNER JOIN FETCH ub.auction a LEFT JOIN FETCH a.highestBid hb"
                    + " WHERE ub.isUserHighest = 1"
                    + " AND ub.bidder.id = :userId";

            s = this.addSortToAuctionsQuery(s, sortOption);

            TypedQuery<Object[]> query = this.em.createQuery(s, Object[].class)
                    .setParameter("userId", userId);

            List<Object[]> values = query.getResultList();
            List<AuctionBidsTuple> result = values.stream()
                    .map(v -> new AuctionBidsTuple((Auction) v[0], (Bid) v[1], (Bid) v[2]))
                    .collect(Collectors.toList());

            return result;
        }
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

                    Category ArtCoins = new Category("Art and Coins");
                    emanager.persist(ArtCoins);
                    Category beautyAndHealthCare = new Category("Beauty and Health care");
                    emanager.persist(beautyAndHealthCare);
                    Category carAccessories = new Category("Car & Accessories");
                    emanager.persist(carAccessories);
                    Category children = new Category("Children");
                    emanager.persist(children);
                    Category computersAndPrograms = new Category("Computers and Programs");
                    emanager.persist(computersAndPrograms);
                    Category electricityAndElectronics = new Category("Electricity and Electronics");
                    emanager.persist(electricityAndElectronics);
                    Category fashion = new Category("Fashion");
                    emanager.persist(fashion);
                    Category forHomeAndGarden = new Category("for Home and Garden");
                    emanager.persist(forHomeAndGarden);
                    Category recreationAndSports = new Category("Recreation and Sports");
                    emanager.persist(recreationAndSports);
                    Category variousGifts = new Category("Everything Else");
                    emanager.persist(variousGifts);

                    Auction a1 = new Auction(yaniv, ArtCoins, "Israeli 100 Pruta coin", LocalDate.now(), 18, 12, new BigDecimal(150.0), new BigDecimal(300.0), new BigDecimal(250.0));
                    a1.setDescription("Israeli 100 Pruta from 1954 (Not magnetic)");
                    a1.setPicture(ImageUtils.loadImage("Israeli100Prutacoin.jpg"), "jpg");
                    emanager.persist(a1);
                    emanager.flush();

                    Auction a2 = new Auction(aharon, ArtCoins, "Israeli Silver Coins", LocalDate.now(), 18, 10, new BigDecimal(1000.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a2.setDescription("Israeli Govenment silver coins");
                    a2.setPicture(ImageUtils.loadImage("IsraeliSilverCoins.jpg"), "jpg");
                    emanager.persist(a2);
                    emanager.flush();

                    Auction a3 = new Auction(yaniv, ArtCoins, "Sheep Head - Menashe Kadishman", LocalDate.now(), 18, 20, new BigDecimal(800.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a3.setDescription("Sheep head 60x50 - Acrylic on canvas - h:60 w:50 cm - signed lower center and again on the reverse");
                    a3.setPicture(ImageUtils.loadImage("SheepHead-MenasheKadishman.jpg"), "jpg");
                    emanager.persist(a3);
                    emanager.flush();
                    
                    Auction a4 = new Auction(yaniv, computersAndPrograms, "Dell computer", LocalDate.now(), 10, 7, new BigDecimal(35.0), new BigDecimal(90.0), new BigDecimal(80.0));
                    a4.setDescription("Dell Inspiron 15 3593 N3593-5066");
                    a4.setPicture(ImageUtils.loadImage("dell.jpg"), "jpg");
                    emanager.persist(a4);
                    emanager.flush();
                    
                    Auction a5 = new Auction(yaniv, computersAndPrograms, "HP computer", LocalDate.now(), 10, 10, new BigDecimal(20.0), new BigDecimal(80.0), new BigDecimal(55.0));
                    a5.setDescription("HP 255 G7 7DB74EA");
                    a5.setPicture(ImageUtils.loadImage("hp.jpg"), "jpg");
                    emanager.persist(a5);
                    emanager.flush();
                    
                    Auction a6 = new Auction(aharon, forHomeAndGarden, "garden hoe", LocalDate.now(), 10, 7, new BigDecimal(9.0), new BigDecimal(17.0), new BigDecimal(14.0));
                    a6.setDescription("Field Hoe with Fiberglass Handle");
                    a6.setPicture(ImageUtils.loadImage("hoe.jpg"), "jpg");
                    emanager.persist(a6);
                    emanager.flush();
                    
                    Auction a7 = new Auction(aharon, forHomeAndGarden, "Electric Kettle", LocalDate.now(), 10, 10, new BigDecimal(5.0), new BigDecimal(12.0), new BigDecimal(9.0));
                    a7.setDescription("Brentwood KT-1610 Cordless Electric Kettle BPA Free, 1 Liter, White");
                    a7.setPicture(ImageUtils.loadImage("kettle.jpg"), "jpg");
                    emanager.persist(a7);
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
                return query + " ORDER BY COALESCE(hb.amount, a.startingAmount) ASC";

            case Current_Price__Descending:
                return query + " ORDER BY COALESCE(hb.amount, a.startingAmount) DESC";

            case Ending_Time__Asecnding:
                return query + " ORDER BY a.closingTime ASC";

            case Ending_Time__Descending:
            default:
                return query + " ORDER BY a.closingTime DESC";
        }

    }
}

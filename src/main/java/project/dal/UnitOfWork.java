/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
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

    public List<Auction> getSortedList(Category category, String sortOption) {
        TypedQuery<Auction> query;

        switch (sortOption) {
            case "current price - ascending order":
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory ORDER BY a.highestBid",
                        Auction.class);
                break;
            case "current price - descending order":
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory ORDER BY a.highestBid DESC",
                        Auction.class);
                break;
            case "ending time - ascending order":
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory ORDER BY a.endingTime",
                        Auction.class);
                break;
            case "ending time - descending order":
            default:
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory ORDER BY a.endingTime DESC",
                        Auction.class);
                break;
        }

        List<Auction> resList = query
                .setParameter("inputCategory", category)
                .getResultList();

        return resList;

    }

    public void delete(Auction chosenAuction) {
        em.remove(chosenAuction);
    }

    public boolean isUniqueName (String userName) {
        try{
            UserProfile userByUserName =
                (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :inputUserName")
                .setParameter("inputUserName", userName)
                .getSingleResult();
        }
        catch (NoResultException e) {
            return true;
        }
        return false;
    }

    public UserProfile getLoginUser(String userName, String password) {
        
        UserProfile loginUser = null;
        try {
            loginUser =
                (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :inputName")
                .setParameter("inputName", userName)
                .getSingleResult();
        }
        catch (NoResultException e) { // no such UserName in DB
            return null;
        }
        
        PasswordHasher hasher = new PasswordHasher();
        boolean isAuthenticate = hasher.authenticate(password, loginUser.getPasswordHash(), loginUser.getPasswordSalt());
        
        if (isAuthenticate)
            return loginUser;
        else
            return null;
    }

    public AuctionListItemDto[] getActiveAuctions(int categoryId, SortOption sortOption, int userId) {

        TypedQuery<Auction> query;

        switch (sortOption) {
            case Current_Price__Ascending:
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory AND NOT a.isClosed ORDER BY a.highestBid",
                        Auction.class);
                break;
            case Current_Price__Descending:
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory AND NOT a.isClosed ORDER BY a.highestBid DESC",
                        Auction.class);
                break;
            case Ending_Time__Asecnding:
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory AND NOT a.isClosed ORDER BY a.endingTime",
                        Auction.class);
                break;
            case Ending_Time__Descending:
            default:
                query = this.em.createQuery(
                        "SELECT a FROM Auction a WHERE a.category = :inputCategory AND NOT a.isClosed ORDER BY a.endingTime DESC",
                        Auction.class);
                break;
        }

        Stream<Auction> auctions = query.getResultStream();
        AuctionListItemDto[] dtos = auctions
                .map(a -> this.mapAuctionToListItemDto(a, userId))
                .toArray(AuctionListItemDto[]::new);

        return dtos;
    }

    private AuctionListItemDto mapAuctionToListItemDto(Auction auction, int userId) {
        AuctionListItemDto dto = new AuctionListItemDto();
        dto.setId(auction.getId());
        dto.setCategory(this.mapCategoryToDto(auction.getCategory()));
        dto.setTitle(auction.getTitle());
        dto.setIsClosed(auction.isIsClosed());
        if (auction.getHighestBid() != null) {
            dto.setLatestBidAmmount(auction.getHighestBid().getAmmount());
        }
        dto.setCanCancel(auction.canCancel(userId));
        dto.setCanEdit(auction.canEdit(userId));

        return dto;
    }

    private CategoryDto mapCategoryToDto(Category category) {
        CategoryDto dto = new CategoryDto(category.getId(), category.getTitle());
        return dto;
    }

    public boolean isEmpty() {
        TypedQuery<Integer> query = this.em.createQuery("Select count(c) from Category c", Integer.class);
        return query.getFirstResult() == 0;
    }

    public void saveChanges() {
        this.transaction.commit();
    }

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

                    PasswordHasher hasher = new PasswordHasher();

                    hasher.hash("shalom");
                    UserProfile yaniv = new UserProfile("yaniv", "Yaniv", "Shalom", "yaniv@gmail.com", "054-56534432", hasher.getHash(), hasher.getSalt());
                    emanager.persist(yaniv);
                    emanager.flush();

                    hasher.hash("gross");
                    UserProfile aharon = new UserProfile("aharon", "Aharon", "Gross", "aharon@gmail.com", "054-56534434", hasher.getHash(), hasher.getSalt());
                    emanager.persist(aharon);
                    emanager.flush();

                    Category israeliCoins = new Category("Israeli Coins");
                    emanager.persist(israeliCoins);
                    emanager.flush();
                    
                    Category israeliArt = new Category("Israeli Art");

                    Auction a1 = new Auction(yaniv, israeliCoins);
                    a1.setTitle("Israeli 100 Pruta coin");
                    a1.setDescription("Israeli 100 Pruta from 1954 (Not magnetic)");
                    a1.setTimes(new Date(), 12);
                    a1.setAmounts(new BigDecimal(150.0), new BigDecimal(300.0), new BigDecimal(250.0));
                    a1.setPicture(ImageIO.read(new File("Israeli 100 Pruta coin.jpg")), "jpg");
                    emanager.persist(a1);
                    emanager.flush();

                    Auction a2 = new Auction(aharon, israeliCoins);
                    a2.setTitle("Israeli Silver Coins");
                    a2.setDescription("Israeli Govenment silver coins");
                    a2.setTimes(new Date(), 10);
                    a2.setAmounts(new BigDecimal(1000.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a2.setPicture(ImageIO.read(new File("Israeli Silver Coins.jpg")), "jpg");
                    emanager.persist(a2);
                    emanager.flush();
                    
                    Auction a3 = new Auction(yaniv, israeliArt);
                    a3.setTitle("Sheep Head - Menashe Kadishman");
                    a3.setDescription("Sheep head 60x50 - Acrylic on canvas - h:60 w:50 cm - signed lower center and again on the reverse");
                    a3.setTimes(new Date(), 20);
                    a3.setAmounts(new BigDecimal(800.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                    a3.setPicture(ImageIO.read(new File("Sheep Head - Menashe Kadishman.jpg")), "jpg");
                    emanager.persist(a3);
                    emanager.flush();

                    emanager.getTransaction().commit();
                    
                } catch (IOException ex) {
                    Logger.getLogger(UnitOfWork.class.getName()).log(Level.SEVERE, null, ex);
                }

                emanager.close();
                emf.close();

                _isInitialized = true;
            }
        }

    }
}

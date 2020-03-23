/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
    
    
    private UnitOfWork(EntityManagerFactory emf, EntityManager em, EntityTransaction transaction)
    {
        this.emf = emf;
        this.em = em;
        this.transaction = transaction;
    }
    
    public static UnitOfWork create() {
        
        seed();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "auctions_persistence_unit" );
        EntityManager em = emf.createEntityManager( );
        EntityTransaction tr = em.getTransaction( );
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
        
        switch(sortOption) {
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
    
    public boolean isRegistered (String email) {
        try{
            UserProfile userByEmail =
                (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE u.email = :inputEmail")
                .setParameter("inputEmail", email)
                .getSingleResult();
        }
        catch (NoResultException e) {
            return false;
        }
        return true;
    }
    
    public UserProfile getLoginUser (String userName, String password) {
        PasswordHasher hasher = new PasswordHasher();
        hasher.hash(password);
        
        try {
            UserProfile loginUser =
                (UserProfile) this.em.createQuery("SELECT u FROM UserProfile u WHERE "
                        + "u.username = :inputName AND u.passwordHash = :inputHash AND u.passwordSalt = :inputSalt")
                .setParameter("inputName", userName)
                .setParameter("inputHash", hasher.getHash())
                .setParameter("inputSalt", hasher.getSalt())
                .getSingleResult();
            return loginUser;
        }
        catch (NoResultException e) {
            return null;
        }
    }
    
    public AuctionListItemDto[] getActiveAuctions(int categoryId, SortOption sortOption, int userId) {
        
        TypedQuery<Auction> query;
        
        switch(sortOption) {
            case Current_Price__Ascending :
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
    
    
    public void saveChanges()
    {
        this.transaction.commit();
    }
    
    public void close()
    {
        this.em.close();
        this.emf.close();
    }

    public void persist(Object obj) {
        this.em.persist(obj);
    }
    
    private static synchronized void seed() {

        if (!_isInitialized) {
            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory( "auctions_persistence_unit" );
            EntityManager emanager = emf.createEntityManager( );
            
            

            if (emanager.createQuery("Select count(c) from Category c", Integer.class).getFirstResult() == 0) {

                emanager.getTransaction().begin();
                
                PasswordHasher hasher = new PasswordHasher();

                hasher.hash("shalom");
                UserProfile yaniv = new UserProfile("yaniv", "Yaniv", "Shalom", hasher.getHash(), hasher.getSalt());
                emanager.persist(yaniv);
                emanager.flush();

                hasher.hash("gross");
                UserProfile aharon = new UserProfile("aharon", "Aharon", "Gross", hasher.getHash(), hasher.getSalt());
                emanager.persist(aharon);
                emanager.flush();

                Category israeliCoins = new Category("Israeli Coins");
                emanager.persist(israeliCoins);
                emanager.flush();

                Auction israeliCoinsAuction = new Auction(yaniv, "100 Israeli ancient coins", "100 Israeli ancient coins from 100 BC", 
                        israeliCoins,  new Date(), 12, new BigDecimal(10000.0), new BigDecimal(20000.0), new BigDecimal(15000.0));
                emanager.persist(israeliCoinsAuction);
                emanager.flush();

                Auction israeliCoinsAuction2 = new Auction(aharon, "200 Israeli coins from 1985", "200 Israeli coins from 1985", 
                        israeliCoins,  new Date(), 10, new BigDecimal(1000.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                emanager.persist(israeliCoinsAuction2);
                emanager.flush();

                emanager.getTransaction().commit();
                
            }
            
            emanager.close();
            emf.close();
    
            _isInitialized = true;
        }
    }
}

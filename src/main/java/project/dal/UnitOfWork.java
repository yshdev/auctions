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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import project.domain.Auction;
import project.domain.Category;
import project.domain.PasswordHasher;
import project.domain.UserProfile;
import project.service.CategoryDto;

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
            EntityManager em = emf.createEntityManager( );
            
            

            if (em.createQuery("Select count(c) from Category c", Integer.class).getFirstResult() == 0) {

                em.getTransaction().begin();
                
                PasswordHasher hasher = new PasswordHasher();

                hasher.hash("shalom");
                UserProfile yaniv = new UserProfile("yaniv", "Yaniv", "Shalom", hasher.getHash(), hasher.getSalt());
                em.persist(yaniv);

                hasher.hash("gross");
                UserProfile aharon = new UserProfile("aharon", "Aharon", "Gross", hasher.getHash(), hasher.getSalt());
                em.persist(aharon);

                Category israeliCoins = new Category("Israeli Coins");
                em.persist(israeliCoins);

                Auction israeliCoinsAuction = new Auction(yaniv, "100 Israeli ancient coins", "100 Israeli ancient coins from 100 BC", 
                        israeliCoins,  new Date(), 12, new BigDecimal(10000.0), new BigDecimal(20000.0), new BigDecimal(15000.0));
                em.persist(israeliCoinsAuction);

                Auction israeliCoinsAuction2 = new Auction(aharon, "200 Israeli coins from 1985", "200 Israeli coins from 1985", 
                        israeliCoins,  new Date(), 10, new BigDecimal(1000.0), new BigDecimal(2000.0), new BigDecimal(1500.0));
                em.persist(israeliCoinsAuction2);

                em.getTransaction().commit();
                
            }
            
            em.close();
            emf.close();
    
            _isInitialized = true;
        }
    }
}

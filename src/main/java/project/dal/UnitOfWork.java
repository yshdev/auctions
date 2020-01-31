/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


/**
 *
 * @author Shalom
 */
public class UnitOfWork {
 
    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final EntityTransaction transaction;
    
    private UnitOfWork(EntityManagerFactory emf, EntityManager em, EntityTransaction transaction)
    {
        this.emf = emf;
        this.em = em;
        this.transaction = transaction;
    }
    
    public static UnitOfWork create() {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "auctions_persistence_unit" );
        EntityManager em = emf.createEntityManager( );
        EntityTransaction tr = em.getTransaction( );
        tr.begin();
        return new UnitOfWork(emf, em, tr);
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
}

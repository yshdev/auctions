/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import project.domain.Category;
import project.service.CategoryDto;


/**
 *
 * @author Shalom
 */
public class UnitOfWork implements AutoCloseable {
 
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
    
    public CategoryDto[] getAllCategories() {
        TypedQuery<Category> query = this.em.createQuery("Select c from Category c", Category.class);
        Stream<Category> categories = query.getResultStream();
        CategoryDto[] dtos = categories
                .map(c -> new CategoryDto(c.getId(), c.getTitle()))
                .toArray(CategoryDto[]::new);
                        
        return dtos;
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
}

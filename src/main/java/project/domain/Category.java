/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Shalom
 */
@Entity
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
        
    private String title;
    
    public Category() {
    }
    
    public Category(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Category title cannot be empty.");
        }
        this.title = title;
    }
       

    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Category[ id=" + id + " ]";
    }
    
    
    
    
}

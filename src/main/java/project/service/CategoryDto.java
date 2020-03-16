/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

import java.io.Serializable;

/**
 *
 * @author Shalom
 */
public class CategoryDto implements Serializable {
    
    private String title;
    private int id;

    public CategoryDto() {
    }

    public CategoryDto(int id, String title) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.service;

/**
 *
 * @author Shalom
 */
public enum SortOption {
    Current_Price__Ascending("current price - ascending order"),
    Current_Price__Descending("current price - descending order"),
    Ending_Time__Asecnding("ending time - ascending order"),
    Ending_Time__Descending("ending time - descending order");
    
    private String label;
    
    private SortOption(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

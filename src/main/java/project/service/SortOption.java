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
    Current_Price__Ascending("Current Price - Ascending"),
    Current_Price__Descending("Current Price - Descending"),
    Ending_Time__Asecnding("Ending Time - Ascending"),
    Ending_Time__Descending("Ending Time - Descending");
    
    private final String label;
    
    private SortOption(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

package project.auctionserver;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named(value = "itemListBean")
@SessionScoped
public class ItemListBean implements Serializable {
    
    private final String[] mainList = {
        "all categories", "mainCategory 1", "mainCategory 2",
        "mainCategory 3", "mainCategory 4", "mainCategory 5"};
    private final String[] subList1 = {"11", "12", "13"};
    private final String[] subList2 = {"21", "22", "23", "24"};
    private final String[] subList3 = {"31", "32"};
    private final String[] subList4 = {"41", "42", "43", "44", "45"};
    private final String[] subList5 = {"51"};
    
    private String mainCategory;
    private String subCategory;

    public ItemListBean() {
        mainCategory = "all categories";
    }
    
    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public String getMainCategory() {
        return mainCategory;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public String[] getMainList() {
        return mainList;
    }
    
    public String[] getSubList() {
        switch (mainCategory) {
            case "mainCategory 1": return subList1;
            case "mainCategory 2": return subList2;
            case "mainCategory 3": return subList3;
            case "mainCategory 4": return subList4;
            case "mainCategory 5": return subList5;
            default: return subList5;
        }
    }
}

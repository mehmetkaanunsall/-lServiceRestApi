/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.10.2016 12:03:38
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class UserDataMenuConnection extends WotLogging {

    private int id;
    private UserData userData;
    private Page page;
    private String  color;
    private String icon;
    private int order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    

   

}

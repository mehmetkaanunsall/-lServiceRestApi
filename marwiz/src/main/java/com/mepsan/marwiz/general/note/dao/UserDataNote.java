/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

/**
 *
 * @author Gozde Gursel
 */
public class UserDataNote extends WotLogging {

    private int id;
    private UserData userData;
    private String description;
    private String formatDate; // tarih bilgisi xhtml tarafında formatlanmadığı için bean tarafında formatlanması için bu alan tutuldu.
    private int itemValue;

    public UserDataNote() {
        userData = new UserData();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public int getItemValue() {
        return itemValue;
    }

    public void setItemValue(int itemValue) {
        this.itemValue = itemValue;
    }

}

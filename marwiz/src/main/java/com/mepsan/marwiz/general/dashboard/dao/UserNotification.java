/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:45:00
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserNotification {

    private int id;
    private String description;
    private int centerWarningTypeId;
    private List<NotificationRecommendedPrice> listOfNotification;
    private int typeId;
    private boolean isCenter;
    

    public UserNotification() {
        this.listOfNotification = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<NotificationRecommendedPrice> getListOfNotification() {
        return listOfNotification;
    }

    public void setListOfNotification(List<NotificationRecommendedPrice> listOfNotification) {
        this.listOfNotification = listOfNotification;
    }

    public int getCenterWarningTypeId() {
        return centerWarningTypeId;
    }

    public void setCenterWarningTypeId(int centerWarningTypeId) {
        this.centerWarningTypeId = centerWarningTypeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public boolean isIsCenter() {
        return isCenter;
    }

    public void setIsCenter(boolean isCenter) {
        this.isCenter = isCenter;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.dao;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

/**
 *
 * @author elif.mart
 */
public class ScheduledTaskProcessesDay extends WotLogging {

    private int id;
    private String name;
    private boolean isSelected;
    private Date workingTime;

    public ScheduledTaskProcessesDay() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Date getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Date workingTime) {
        this.workingTime = workingTime;
    }
    
     @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

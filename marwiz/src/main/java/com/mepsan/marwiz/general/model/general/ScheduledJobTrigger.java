/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.general;

import org.quartz.JobKey;

/**
 *
 * @author esra.cabuk
 */
public class ScheduledJobTrigger {
    
    private int id;
    private String name;
    private String cronstring;
    private int branch_id;
    private int type_id;
    private int repeatCount;
    private String days;
    private String daysDate;

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

    public String getCronstring() {
        return cronstring;
    }

    public void setCronstring(String cronstring) {
        this.cronstring = cronstring;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDaysDate() {
        return daysDate;
    }

    public void setDaysDate(String daysDate) {
        this.daysDate = daysDate;
    }
    
    public JobKey getKey() {
        return new JobKey(id + "_" + branch_id);
    }
    
    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
    
}

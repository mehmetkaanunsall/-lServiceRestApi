/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public class ScheduledTaskProcesses extends WotLogging {

    private int id;
    private Branch branch;
    private Type type;
    private String name;
    private Status status;
    private String description;
    private String days;
    private String daysDate;
    private String convertDays;
    private Date workingTime;

    public ScheduledTaskProcesses() {
        this.branch = new Branch();
        this.type = new Type();
        this.status = new Status();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getConvertDays() {
        return convertDays;
    }

    public void setConvertDays(String convertDays) {
        this.convertDays = convertDays;
    }

   
    public Date getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Date workingTime) {
        this.workingTime = workingTime;
    }

}

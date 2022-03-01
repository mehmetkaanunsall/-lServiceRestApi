/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public class OrderReport {
    
    private Date beginDate;
    private Date endDate;
    private List<Account> accountList;
    private List<Stock> stockList;
    private List<BranchSetting> selectedBranchList;
    private boolean isCheckItem;
    private Type orderType;
    private int typeNo;
    
    public OrderReport() {
        this.accountList = new ArrayList<>();
        this.selectedBranchList = new ArrayList<>();
        this.stockList = new ArrayList<>();
        this.orderType = new Type();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public boolean isIsCheckItem() {
        return isCheckItem;
    }

    public void setIsCheckItem(boolean isCheckItem) {
        this.isCheckItem = isCheckItem;
    }

    public Type getOrderType() {
        return orderType;
    }

    public void setOrderType(Type orderType) {
        this.orderType = orderType;
    }

    public int getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(int typeNo) {
        this.typeNo = typeNo;
    }
     
 
}

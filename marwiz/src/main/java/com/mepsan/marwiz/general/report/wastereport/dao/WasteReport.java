/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public class WasteReport extends WarehouseItem {

    private Date beginDate;
    private Date endDate;
    private List<Categorization> categorizationList;
    private String category;
    private List<Stock> stockList;
    private BigDecimal purchasePrice;
    private Currency currency;
    private BigDecimal total;
    private String wasteCause;
    private Date expirationDate;
    private BigDecimal taxRate;
    private WasteReason wasteReason;
    private BranchSetting branchSetting;
    

    public WasteReport() {
        this.categorizationList = new ArrayList<>();
        this.stockList = new ArrayList<>();
        this.currency = new Currency();
        this.wasteReason = new WasteReason();
        this.branchSetting = new BranchSetting();
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

    public List<Categorization> getCategorizationList() {
        return categorizationList;
    }

    public void setCategorizationList(List<Categorization> categorizationList) {
        this.categorizationList = categorizationList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getWasteCause() {
        return wasteCause;
    }

    public void setWasteCause(String wasteCause) {
        this.wasteCause = wasteCause;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public WasteReason getWasteReason() {
        return wasteReason;
    }

    public void setWasteReason(WasteReason wasteReason) {
        this.wasteReason = wasteReason;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

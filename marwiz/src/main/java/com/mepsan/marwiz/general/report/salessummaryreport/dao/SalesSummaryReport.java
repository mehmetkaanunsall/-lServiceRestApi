/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:01:30
 */
package com.mepsan.marwiz.general.report.salessummaryreport.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesSummaryReport extends SaleItem {

    private Date beginDate;
    private Date endDate;
    private BigDecimal countQuantity;
    private BigDecimal totalGiroByStock;
    private BigDecimal totalCountByStock;
    private List<Stock> stockList;
    private List<Categorization> listOfCategorization;
    private String category;
    private BranchSetting branchSetting;
    private BigDecimal totalGiro;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private Account account;

    public SalesSummaryReport() {
        this.stockList = new ArrayList<>();
        this.listOfCategorization = new ArrayList<>();
        this.branchSetting = new BranchSetting();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.account = new Account();
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

    public BigDecimal getCountQuantity() {
        return countQuantity;
    }

    public void setCountQuantity(BigDecimal countQuantity) {
        this.countQuantity = countQuantity;
    }

    public BigDecimal getTotalGiroByStock() {
        return totalGiroByStock;
    }

    public void setTotalGiroByStock(BigDecimal totalGiroByStock) {
        this.totalGiroByStock = totalGiroByStock;
    }

    public BigDecimal getTotalCountByStock() {
        return totalCountByStock;
    }

    public void setTotalCountByStock(BigDecimal totalCountByStock) {
        this.totalCountByStock = totalCountByStock;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public BigDecimal getTotalGiro() {
        return totalGiro;
    }

    public void setTotalGiro(BigDecimal totalGiro) {
        this.totalGiro = totalGiro;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

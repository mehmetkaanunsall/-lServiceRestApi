/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:08:02
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseSummaryReport extends InvoiceItem {

    private Date beginDate;
    private Date endDate;
    private List<Stock> stockList;
    private String category;

    private BigDecimal totalQuantityByStock;
    private BigDecimal totalMoneyByStock;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private BranchSetting branchSetting;
    private BigDecimal unitPriceWithTax;
    private BigDecimal premiumAmount;

    public PurchaseSummaryReport() {
        stockList = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
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

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public BigDecimal getTotalQuantityByStock() {
        return totalQuantityByStock;
    }

    public void setTotalQuantityByStock(BigDecimal totalQuantityByStock) {
        this.totalQuantityByStock = totalQuantityByStock;
    }

    public BigDecimal getTotalMoneyByStock() {
        return totalMoneyByStock;
    }

    public void setTotalMoneyByStock(BigDecimal totalMoneyByStock) {
        this.totalMoneyByStock = totalMoneyByStock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public BigDecimal getUnitPriceWithTax() {
        return unitPriceWithTax;
    }

    public void setUnitPriceWithTax(BigDecimal unitPriceWithTax) {
        this.unitPriceWithTax = unitPriceWithTax;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
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

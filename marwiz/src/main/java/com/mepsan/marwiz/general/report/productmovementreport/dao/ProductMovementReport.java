/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 10:52:05
 */
package com.mepsan.marwiz.general.report.productmovementreport.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductMovementReport {

    private int id;
    private Date beginDate;
    private Date endDate;
    private BigDecimal minSalesAmount;
    private BigDecimal maxSalesAmount;
    private Stock stock;
    private BigDecimal quantity;
    private List<Categorization> listOfCategorization;
    private BigDecimal unitPrice;
    private BigDecimal salesPrice;
    private Currency currency;
    private String category;
    private List<Stock> stockList;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<BranchSetting> listOfBranch;
    private Branch branch;

    public ProductMovementReport() {
        this.stock = new Stock();
        this.listOfCategorization = new ArrayList<>();
        this.currency = new Currency();
        this.stockList = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.listOfBranch = new ArrayList<>();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public BigDecimal getMinSalesAmount() {
        return minSalesAmount;
    }

    public void setMinSalesAmount(BigDecimal minSalesAmount) {
        this.minSalesAmount = minSalesAmount;
    }

    public BigDecimal getMaxSalesAmount() {
        return maxSalesAmount;
    }

    public void setMaxSalesAmount(BigDecimal maxSalesAmount) {
        this.maxSalesAmount = maxSalesAmount;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }

}

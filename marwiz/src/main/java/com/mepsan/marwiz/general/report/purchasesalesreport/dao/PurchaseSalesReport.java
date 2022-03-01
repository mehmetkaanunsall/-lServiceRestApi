/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 01.10.2018 18:07:00
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseSalesReport {

    private int id;
    private Date beginDate;
    private Date endDate;
    private Stock stock;
    private BigDecimal quantity;
    private List<Categorization> listOfCategorization;
    private BigDecimal salesPrice;
    private Currency currency;
    private String category;
    private List<Stock> stockList;
    private TaxGroup stocktaxgroup;
    private List<TaxGroup> taxGroupList;
    private Account account;
    private boolean isPurchase;
    private boolean isReturn;

    private BigDecimal totalMoney;
    private BigDecimal totalTax;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal avgSaleUnitPrice;
    private BigDecimal lastSaleUnitPrice;
    private BigDecimal lastPurchaseUnitPrice;
    private BigDecimal avgPurchaseUnitPrice;
    private BigDecimal unitPrice;
    private BranchSetting branchSetting;
    private BigDecimal purchaseCost;
    private BigDecimal salesTotalMoney;
    private String stringResult;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private BigDecimal profitAmount;
    private BigDecimal profitpercentage;
    private int costType;
    private BigDecimal cost;

    public PurchaseSalesReport() {
        this.stock = new Stock();
        this.listOfCategorization = new ArrayList<>();
        this.currency = new Currency();
        this.stockList = new ArrayList<>();
        this.stocktaxgroup = new TaxGroup();
        this.taxGroupList = new ArrayList<>();
        this.account = new Account();
        this.branchSetting = new BranchSetting();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
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

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public TaxGroup getStocktaxgroup() {
        return stocktaxgroup;
    }

    public void setStocktaxgroup(TaxGroup stocktaxgroup) {
        this.stocktaxgroup = stocktaxgroup;
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

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getAvgSaleUnitPrice() {
        return avgSaleUnitPrice;
    }

    public void setAvgSaleUnitPrice(BigDecimal avgSaleUnitPrice) {
        this.avgSaleUnitPrice = avgSaleUnitPrice;
    }

    public BigDecimal getLastSaleUnitPrice() {
        return lastSaleUnitPrice;
    }

    public void setLastSaleUnitPrice(BigDecimal lastSaleUnitPrice) {
        this.lastSaleUnitPrice = lastSaleUnitPrice;
    }

    public BigDecimal getLastPurchaseUnitPrice() {
        return lastPurchaseUnitPrice;
    }

    public void setLastPurchaseUnitPrice(BigDecimal lastPurchaseUnitPrice) {
        this.lastPurchaseUnitPrice = lastPurchaseUnitPrice;
    }

    public BigDecimal getAvgPurchaseUnitPrice() {
        return avgPurchaseUnitPrice;
    }

    public void setAvgPurchaseUnitPrice(BigDecimal avgPurchaseUnitPrice) {
        this.avgPurchaseUnitPrice = avgPurchaseUnitPrice;
    }

    public List<TaxGroup> getTaxGroupList() {
        return taxGroupList;
    }

    public void setTaxGroupList(List<TaxGroup> taxGroupList) {
        this.taxGroupList = taxGroupList;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public boolean isIsReturn() {
        return isReturn;
    }

    public void setIsReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(BigDecimal purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public BigDecimal getSalesTotalMoney() {
        return salesTotalMoney;
    }

    public void setSalesTotalMoney(BigDecimal salesTotalMoney) {
        this.salesTotalMoney = salesTotalMoney;
    }

    public String getStringResult() {
        return stringResult;
    }

    public void setStringResult(String stringResult) {
        this.stringResult = stringResult;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitpercentage() {
        return profitpercentage;
    }

    public void setProfitpercentage(BigDecimal profitpercentage) {
        this.profitpercentage = profitpercentage;
    }

    public int getCostType() {
        return costType;
    }

    public void setCostType(int costType) {
        this.costType = costType;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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

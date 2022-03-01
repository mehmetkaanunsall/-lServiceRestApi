/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.dao;

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

/**
 *
 * @author esra.cabuk
 */
public class StockInventoryReport {

    private Date date;
    private int reportType;
    private int cost;
    private boolean isTax;
    private boolean zeroStock;
    private boolean minusStock;
    private boolean retailStock;
    private boolean fuelStock;
    private List<Categorization> listOfStockCategorization;
    private List<Account> listOfAccount;
    private Stock stock;
    private BigDecimal quantity;
    private BigDecimal lasPurchasePrice;
    private Currency lastPurchaseCurreny;
    private BigDecimal lastPurchaseCost;
    private BigDecimal lastSalePrice;
    private Currency lastSaleCurreny;
    private BigDecimal lastSaleCost;
    private TaxGroup tax;
    private String category;
    private BranchSetting branchSetting;
    private boolean onlyMinusStock;
    private boolean onlyNotForSaleStock;
    private List<Stock> listOfStock;
    private List<CentralSupplier> listOfCentralSupplier;

    public StockInventoryReport() {
        this.stock = new Stock();
        this.listOfAccount = new ArrayList<>();
        this.listOfStockCategorization = new ArrayList<>();
        this.lastPurchaseCurreny = new Currency();
        this.lastSaleCurreny = new Currency();
        this.tax = new TaxGroup();
        this.branchSetting = new BranchSetting();
        this.listOfStock = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isIsTax() {
        return isTax;
    }

    public void setIsTax(boolean isTax) {
        this.isTax = isTax;
    }

    public boolean isZeroStock() {
        return zeroStock;
    }

    public void setZeroStock(boolean zeroStock) {
        this.zeroStock = zeroStock;
    }

    public boolean isMinusStock() {
        return minusStock;
    }

    public void setMinusStock(boolean minusStock) {
        this.minusStock = minusStock;
    }

    public boolean isRetailStock() {
        return retailStock;
    }

    public void setRetailStock(boolean retailStock) {
        this.retailStock = retailStock;
    }

    public List<Categorization> getListOfStockCategorization() {
        return listOfStockCategorization;
    }

    public void setListOfStockCategorization(List<Categorization> listOfStockCategorization) {
        this.listOfStockCategorization = listOfStockCategorization;
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

    public BigDecimal getLasPurchasePrice() {
        return lasPurchasePrice;
    }

    public void setLasPurchasePrice(BigDecimal lasPurchasePrice) {
        this.lasPurchasePrice = lasPurchasePrice;
    }

    public BigDecimal getLastPurchaseCost() {
        return lastPurchaseCost;
    }

    public void setLastPurchaseCost(BigDecimal lastPurchaseCost) {
        this.lastPurchaseCost = lastPurchaseCost;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public BigDecimal getLastSaleCost() {
        return lastSaleCost;
    }

    public void setLastSaleCost(BigDecimal lastSaleCost) {
        this.lastSaleCost = lastSaleCost;
    }

    public TaxGroup getTax() {
        return tax;
    }

    public void setTax(TaxGroup tax) {
        this.tax = tax;
    }

    public Currency getLastPurchaseCurreny() {
        return lastPurchaseCurreny;
    }

    public void setLastPurchaseCurreny(Currency lastPurchaseCurreny) {
        this.lastPurchaseCurreny = lastPurchaseCurreny;
    }

    public Currency getLastSaleCurreny() {
        return lastSaleCurreny;
    }

    public void setLastSaleCurreny(Currency lastSaleCurreny) {
        this.lastSaleCurreny = lastSaleCurreny;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isOnlyMinusStock() {
        return onlyMinusStock;
    }

    public void setOnlyMinusStock(boolean onlyMinusStock) {
        this.onlyMinusStock = onlyMinusStock;
    }

    public boolean isOnlyNotForSaleStock() {
        return onlyNotForSaleStock;
    }

    public void setOnlyNotForSaleStock(boolean onlyNotForSaleStock) {
        this.onlyNotForSaleStock = onlyNotForSaleStock;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public boolean isFuelStock() {
        return fuelStock;
    }

    public void setFuelStock(boolean fuelStock) {
        this.fuelStock = fuelStock;
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

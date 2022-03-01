/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 12:22:08
 */
package com.mepsan.marwiz.general.report.fulltakingreport.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FullTakingReport {

    private int id;
    private Warehouse warehouse;
    private StockTaking stockTaking;
    private Stock stock;
    private BigDecimal quantity;
    private BigDecimal warehouseQuantity;
    private BigDecimal difference;
    private BigDecimal lastSalePrice;
    private BigDecimal lastPurchasePrice;
    private Currency lastPurchaseCurrency;
    private Currency lastSaleCurrency;
    private List<Stock> stockList;
    private List<Categorization> listOfCategorization;
    private int differentStatus;//0 Tam 1: Fazla -1 Eksik
    private int purchaseTaxRate;
    private int saleTaxRate;
    private String subCategories;
    private String parentCategories;
    private BigDecimal price;
    private Currency priceCurrency;
    private BigDecimal systemPrice;
    private BigDecimal enteredPrice;
    private BigDecimal differentPrice;

    public FullTakingReport() {
        this.warehouse = new Warehouse();
        this.stockTaking = new StockTaking();
        this.stock = new Stock();
        this.stockList = new ArrayList<>();
        this.listOfCategorization = new ArrayList<>();
        this.lastPurchaseCurrency = new Currency();
        this.lastSaleCurrency = new Currency();
        this.priceCurrency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
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

    public BigDecimal getLastPurchasePrice() {
        return lastPurchasePrice;
    }

    public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
        this.lastPurchasePrice = lastPurchasePrice;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public BigDecimal getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public void setWarehouseQuantity(BigDecimal warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;
    }

    public int getDifferentStatus() {
        return differentStatus;
    }

    public void setDifferentStatus(int differentStatus) {
        this.differentStatus = differentStatus;
    }

    public int getPurchaseTaxRate() {
        return purchaseTaxRate;
    }

    public void setPurchaseTaxRate(int purchaseTaxRate) {
        this.purchaseTaxRate = purchaseTaxRate;
    }

    public int getSaleTaxRate() {
        return saleTaxRate;
    }

    public void setSaleTaxRate(int saleTaxRate) {
        this.saleTaxRate = saleTaxRate;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public String getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(String subCategories) {
        this.subCategories = subCategories;
    }

    public String getParentCategories() {
        return parentCategories;
    }

    public void setParentCategories(String parentCategories) {
        this.parentCategories = parentCategories;
    }

    public Currency getLastPurchaseCurrency() {
        return lastPurchaseCurrency;
    }

    public void setLastPurchaseCurrency(Currency lastPurchaseCurrency) {
        this.lastPurchaseCurrency = lastPurchaseCurrency;
    }

    public Currency getLastSaleCurrency() {
        return lastSaleCurrency;
    }

    public void setLastSaleCurrency(Currency lastSaleCurrency) {
        this.lastSaleCurrency = lastSaleCurrency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(Currency priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public BigDecimal getSystemPrice() {
        return systemPrice;
    }

    public void setSystemPrice(BigDecimal systemPrice) {
        this.systemPrice = systemPrice;
    }

    public BigDecimal getEnteredPrice() {
        return enteredPrice;
    }

    public void setEnteredPrice(BigDecimal enteredPrice) {
        this.enteredPrice = enteredPrice;
    }

    public BigDecimal getDifferentPrice() {
        return differentPrice;
    }

    public void setDifferentPrice(BigDecimal differentPrice) {
        this.differentPrice = differentPrice;
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

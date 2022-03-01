/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:07:46
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MovementReportBetweenWarehouseTakings {

    private Stock stock;
    private StockTaking stockTaking1;
    private StockTaking stockTaking2;
    private BigDecimal stockTaking1Quantity;
    private BigDecimal stockTaking2Quantity;
    private BigDecimal entryAmount;
    private BigDecimal exitamount;
    private BigDecimal difference;
    private BigDecimal result;//(taking2-taking1)-(entry-exit) 
    private BigDecimal lastSalePrice;
    private BigDecimal lastPurchasePrice;
    private Currency lastPurchaseCurrency;
    private Currency lastSaleCurrency;
    private int purchaseTaxRate;
    private int saleTaxRate;
    private int resultStatus;
    private List<Stock> stockList;
    private List<Categorization> listOfCategorization;

    public MovementReportBetweenWarehouseTakings() {
        this.stock = new Stock();
        this.stockTaking1 = new StockTaking();
        this.stockTaking2 = new StockTaking();
        this.lastPurchaseCurrency = new Currency();
        this.lastSaleCurrency = new Currency();
        this.stockList=new ArrayList<>();
        this.listOfCategorization=new ArrayList<>();
    }

    public StockTaking getStockTaking1() {
        return stockTaking1;
    }

    public void setStockTaking1(StockTaking stockTaking1) {
        this.stockTaking1 = stockTaking1;
    }

    public StockTaking getStockTaking2() {
        return stockTaking2;
    }

    public void setStockTaking2(StockTaking stockTaking2) {
        this.stockTaking2 = stockTaking2;
    }

    public BigDecimal getEntryAmount() {
        return entryAmount;
    }

    public void setEntryAmount(BigDecimal entryAmount) {
        this.entryAmount = entryAmount;
    }

    public BigDecimal getExitamount() {
        return exitamount;
    }

    public void setExitamount(BigDecimal exitamount) {
        this.exitamount = exitamount;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getStockTaking1Quantity() {
        return stockTaking1Quantity;
    }

    public void setStockTaking1Quantity(BigDecimal stockTaking1Quantity) {
        this.stockTaking1Quantity = stockTaking1Quantity;
    }

    public BigDecimal getStockTaking2Quantity() {
        return stockTaking2Quantity;
    }

    public void setStockTaking2Quantity(BigDecimal stockTaking2Quantity) {
        this.stockTaking2Quantity = stockTaking2Quantity;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public BigDecimal getLastPurchasePrice() {
        return lastPurchasePrice;
    }

    public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
        this.lastPurchasePrice = lastPurchasePrice;
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

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
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
    
    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 03:57:46
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StockTrackingReport extends WarehouseItem {

    private BigDecimal salesPrice;
    private Currency currency;
    private BigDecimal purchasePrice;
    private Currency purchaseCurrency;
    private BigDecimal salesPriceWithOutTax;
    private BigDecimal purchasePriceWithTax ;

    private List<Stock> listOfStock;
    private List<Warehouse> listOfWarehouse;
    private String category;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private Branch branch;

    public StockTrackingReport() {
        this.currency = new Currency();
        listOfStock = new ArrayList<>();
        listOfWarehouse = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.branch = new Branch();
        this.purchaseCurrency = new Currency();
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public BigDecimal getSalesPriceWithOutTax() {
        return salesPriceWithOutTax;
    }

    public void setSalesPriceWithOutTax(BigDecimal salesPriceWithOutTax) {
        this.salesPriceWithOutTax = salesPriceWithOutTax;
    }

    public BigDecimal getPurchasePriceWithTax() {
        return purchasePriceWithTax;
    }

    public void setPurchasePriceWithTax(BigDecimal purchasePriceWithTax) {
        this.purchasePriceWithTax = purchasePriceWithTax;
    }    
    
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Currency getPurchaseCurrency() {
        return purchaseCurrency;
    }

    public void setPurchaseCurrency(Currency purchaseCurrency) {
        this.purchaseCurrency = purchaseCurrency;
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

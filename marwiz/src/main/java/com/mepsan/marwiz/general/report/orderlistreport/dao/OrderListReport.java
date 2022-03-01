/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 04:54:02
 */
package com.mepsan.marwiz.general.report.orderlistreport.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.util.ArrayList;
import java.util.List;

public class OrderListReport extends WarehouseItem {

    private List<Warehouse> warehouseList;
    private List<Stock> stockList;
    private List<Categorization> listOfCategorization;
    private String category;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private Branch branch;

    public OrderListReport() {
        this.warehouseList = new ArrayList<>();
        this.stockList = new ArrayList<>();
        this.listOfCategorization = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.branch = new Branch();
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return this.getId();
    }
}

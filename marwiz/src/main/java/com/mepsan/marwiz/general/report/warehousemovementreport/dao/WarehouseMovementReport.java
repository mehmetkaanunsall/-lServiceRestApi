/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WasteItemInfo;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public class WarehouseMovementReport {

    private int id;
    private Date beginDate;
    private Date endDate;
    private List<Warehouse> selectedWarehouses;
    private List<Stock> selectedStocks;
    private Date processDate;
    private Warehouse warehouse;
    private int isDirection;
    private Stock stock;
    private BigDecimal quantity;
    private String category;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private BigDecimal unitPrice;
    private BigDecimal totalTax;
    private BigDecimal totalMoney;
    private Currency currency;
    private Type type;
       

    public WarehouseMovementReport() {
        this.warehouse = new Warehouse();
        this.stock = new Stock();
        this.selectedStocks = new ArrayList<>();
        this.selectedWarehouses = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.currency = new Currency();
        this.type = new Type();
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

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public int getIsDirection() {
        return isDirection;
    }

    public void setIsDirection(int isDirection) {
        this.isDirection = isDirection;
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

    public List<Warehouse> getSelectedWarehouses() {
        return selectedWarehouses;
    }

    public void setSelectedWarehouses(List<Warehouse> selectedWarehouses) {
        this.selectedWarehouses = selectedWarehouses;
    }

    public List<Stock> getSelectedStocks() {
        return selectedStocks;
    }

    public void setSelectedStocks(List<Stock> selectedStocks) {
        this.selectedStocks = selectedStocks;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

   
    @Override
    public String toString() {
        return this.warehouse.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

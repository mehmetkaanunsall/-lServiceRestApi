/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public class IncomingInvoicesItem extends InvoiceItem {

    private String oldStockName;
    private String oldUnitName;
    private boolean isThereUnit;
    private StockEInvoiceUnitCon stockEInvoiceUnitCon;
    private String oldStockEntegrationCode;
    private Unit oldUnit;
    private List<Warehouse> listOfFuelWarehouse;
    private boolean isListFuelWarehouse;

    public void IncomingInvoiceItem() {

        this.stockEInvoiceUnitCon = new StockEInvoiceUnitCon();
        this.oldUnit = new Unit();
        this.listOfFuelWarehouse = new ArrayList<>();

    }

    public boolean isIsThereUnit() {
        return isThereUnit;
    }

    public void setIsThereUnit(boolean isThereUnit) {
        this.isThereUnit = isThereUnit;
    }

    public String getOldUnitName() {
        return oldUnitName;
    }

    public void setOldUnitName(String oldUnitName) {
        this.oldUnitName = oldUnitName;
    }

    public String getOldStockName() {
        return oldStockName;
    }

    public void setOldStockName(String oldStockName) {
        this.oldStockName = oldStockName;
    }

    public StockEInvoiceUnitCon getStockEInvoiceUnitCon() {
        return stockEInvoiceUnitCon;
    }

    public void setStockEInvoiceUnitCon(StockEInvoiceUnitCon stockEInvoiceUnitCon) {
        this.stockEInvoiceUnitCon = stockEInvoiceUnitCon;
    }

    public String getOldStockEntegrationCode() {
        return oldStockEntegrationCode;
    }

    public void setOldStockEntegrationCode(String oldStockEntegrationCode) {
        this.oldStockEntegrationCode = oldStockEntegrationCode;
    }

    public Unit getOldUnit() {
        return oldUnit;
    }

    public void setOldUnit(Unit oldUnit) {
        this.oldUnit = oldUnit;
    }

    public List<Warehouse> getListOfFuelWarehouse() {
        return listOfFuelWarehouse;
    }

    public void setListOfFuelWarehouse(List<Warehouse> listOfFuelWarehouse) {
        this.listOfFuelWarehouse = listOfFuelWarehouse;
    }

    public boolean isIsListFuelWarehouse() {
        return isListFuelWarehouse;
    }

    public void setIsListFuelWarehouse(boolean isListFuelWarehouse) {
        this.isListFuelWarehouse = isListFuelWarehouse;
    }
    
    

}

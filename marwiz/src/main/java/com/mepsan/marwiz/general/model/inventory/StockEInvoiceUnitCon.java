/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;

/**
 *
 * @author elif.mart
 */
public class StockEInvoiceUnitCon {

    private int id;
    private Branch branch;
    private int stockId;
    private String stockIntegrationCode;
    private BigDecimal quantity;

    public StockEInvoiceUnitCon() {
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getStockIntegrationCode() {
        return stockIntegrationCode;
    }

    public void setStockIntegrationCode(String stockIntegrationCode) {
        this.stockIntegrationCode = stockIntegrationCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}

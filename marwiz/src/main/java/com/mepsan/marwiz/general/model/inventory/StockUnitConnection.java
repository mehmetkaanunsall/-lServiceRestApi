/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

/**
 *
 * @author esra.cabuk
 */
public class StockUnitConnection extends WotLogging {

    private int id;
    private Stock stock;
    private Unit unit;
    private BigDecimal quantity;
    private boolean isOtherBranch;

    public StockUnitConnection() {
        this.stock = new Stock();
        this.unit = new Unit();
    }

    public StockUnitConnection(Stock stock, Unit unit, int id) {
        this.stock = stock;
        this.unit = unit;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isIsOtherBranch() {
        return isOtherBranch;
    }

    public void setIsOtherBranch(boolean isOtherBranch) {
        this.isOtherBranch = isOtherBranch;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;

/**
 *
 * @author elif.mart
 */
public class ErrorItems {
    
    private int id;
    private Stock stock;
    private int errorCode;
    private String errorString;
    private int type;
    private BigDecimal quantity;

    public ErrorItems(){

        this.stock = new Stock();

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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return this.stock.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author elif.mart
 */
public class SapStockControlProcess {

    private int id;
    private boolean isSuccess;
    private String getData;
    private Date processDate;
    private String message;
    private String itemJson;
    private String sendData;

    private String sapStockCode;
    private BigDecimal sapQuantity;
    private Unit sapUnit;
    private String centerStockCode;
    private Stock stock;
    private int errorCode;
    private BigDecimal marwizQuantity;

    public SapStockControlProcess() {
        this.stock = new Stock();
        this.sapUnit = new Unit();
    }

    public boolean isIsSuccess() {
        return isSuccess;

    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getGetData() {
        return getData;
    }

    public void setGetData(String getData) {
        this.getData = getData;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getItemJson() {
        return itemJson;
    }

    public void setItemJson(String itemJson) {
        this.itemJson = itemJson;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public String getSapStockCode() {
        return sapStockCode;
    }

    public void setSapStockCode(String sapStockCode) {
        this.sapStockCode = sapStockCode;
    }

    public BigDecimal getSapQuantity() {
        return sapQuantity;
    }

    public void setSapQuantity(BigDecimal sapQuantity) {
        this.sapQuantity = sapQuantity;
    }

    public String getCenterStockCode() {
        return centerStockCode;
    }

    public void setCenterStockCode(String centerStockCode) {
        this.centerStockCode = centerStockCode;
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

    public BigDecimal getMarwizQuantity() {
        return marwizQuantity;
    }

    public void setMarwizQuantity(BigDecimal marwizQuantity) {
        this.marwizQuantity = marwizQuantity;
    }

    public Unit getSapUnit() {
        return sapUnit;
    }

    public void setSapUnit(Unit sapUnit) {
        this.sapUnit = sapUnit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
      @Override
    public String toString() {
        return this.getCenterStockCode();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

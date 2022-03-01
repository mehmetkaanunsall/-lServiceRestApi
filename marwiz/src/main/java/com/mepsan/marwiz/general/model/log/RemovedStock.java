/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 11.12.2018 15:51:35
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

public class RemovedStock {

    private int id;
    private Stock stock;
    private Shift shift;
    private UserData userData;
    private Currency currency;
    private Date processDate;
    private BigDecimal unitPrice;
    private BigDecimal oldValue;
    private BigDecimal newValue;
    private BigDecimal removedValue;
    private BigDecimal removedTotalPrice;
    private String category;

    public RemovedStock() {
        this.stock = new Stock();
        this.shift = new Shift();
        this.userData = new UserData();
        this.currency = new Currency();

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

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public void setOldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    public void setNewValue(BigDecimal newValue) {
        this.newValue = newValue;
    }

    public BigDecimal getRemovedValue() {
        return removedValue;
    }

    public void setRemovedValue(BigDecimal removedValue) {
        this.removedValue = removedValue;
    }

    public BigDecimal getRemovedTotalPrice() {
        return removedTotalPrice;
    }

    public void setRemovedTotalPrice(BigDecimal removedTotalPrice) {
        this.removedTotalPrice = removedTotalPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}

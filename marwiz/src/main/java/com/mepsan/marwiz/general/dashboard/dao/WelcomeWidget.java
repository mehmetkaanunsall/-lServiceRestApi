/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;

/**
 *
 * @author esra.cabuk
 */
public class WelcomeWidget {

    private BigDecimal totalPrice;
    private Type type;
    private String shiftNo;
    private Currency currency;

    public WelcomeWidget() {
        this.type = new Type();
        this.currency = new Currency();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

}

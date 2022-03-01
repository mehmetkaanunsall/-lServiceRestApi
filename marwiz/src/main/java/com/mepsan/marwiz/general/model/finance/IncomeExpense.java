/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpense extends WotLogging {

    private int id;
    private String name;
    private boolean isIncome;
    private IncomeExpense parentId;
    private boolean isProfitMarginReport;
    private BigDecimal balance;

    private BigDecimal totalPrice;
    private Currency totalCurrency;
    private BigDecimal totalExchagePrice;

    public IncomeExpense() {
        this.totalCurrency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }

    public IncomeExpense getParentId() {
        return parentId;
    }

    public void setParentId(IncomeExpense parentId) {
        this.parentId = parentId;
    }

    public boolean isIsProfitMarginReport() {
        return isProfitMarginReport;
    }

    public void setIsProfitMarginReport(boolean isProfitMarginReport) {
        this.isProfitMarginReport = isProfitMarginReport;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Currency getTotalCurrency() {
        return totalCurrency;
    }

    public void setTotalCurrency(Currency totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    public BigDecimal getTotalExchagePrice() {
        return totalExchagePrice;
    }

    public void setTotalExchagePrice(BigDecimal totalExchagePrice) {
        this.totalExchagePrice = totalExchagePrice;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

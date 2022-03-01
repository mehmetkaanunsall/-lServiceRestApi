/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseMovement {
    
    private int id;
    private IncomeExpense incomeExpense;
    private FinancingDocument financingDocument;
    private BigDecimal price;
    private Currency currency;
    private BigDecimal exchangeRate;
    private Date movementDate;
    
     public IncomeExpenseMovement(int id) {
        this.id = id;
    }

    public IncomeExpenseMovement() {
        this.incomeExpense = new IncomeExpense();
        this.financingDocument = new FinancingDocument();
        this.currency=new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IncomeExpense getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Date movementDate) {
        this.movementDate = movementDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return this.financingDocument.getDocumentNumber();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

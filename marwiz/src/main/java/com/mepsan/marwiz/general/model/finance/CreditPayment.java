/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Gozde Gursel
 */
public class CreditPayment extends WotLogging {

    private int id;
    private Credit credit;
    private Type type;
    private FinancingDocument financingDocument;
    private ChequeBill chequeBill;
    private BigDecimal price;
    private Date processDate;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BankAccount bankAccount;
    private Safe safe;
    private Account account;
    private boolean isDirection;

    public CreditPayment() {
        this.credit = new Credit();
        this.currency = new Currency();
        this.type = new Type();
        this.financingDocument = new FinancingDocument();
        this.bankAccount = new BankAccount();
        this.safe = new Safe();
        this.chequeBill = new ChequeBill();
        this.account = new Account();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
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

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public ChequeBill getChequeBill() {
        return chequeBill;
    }

    public void setChequeBill(ChequeBill chequeBill) {
        this.chequeBill = chequeBill;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }


}

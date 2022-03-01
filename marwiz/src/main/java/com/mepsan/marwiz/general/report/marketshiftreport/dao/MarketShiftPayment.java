/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.10.2018 02:36:22
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class MarketShiftPayment extends WotLogging {

    private int id;
    private Account account;
    private Shift shift;
    private BigDecimal salesPrice;
    private BigDecimal actualSalesPrice;
    private Currency currency;
    private Type saleType;
    private Safe safe;
    private BankAccount bankAccount;
    private BigDecimal exchangeRate;
    private Date processDate;
    private boolean is_check;
    private BigDecimal inheritedMoney;

    private BigDecimal differenceAmount;
    private BigDecimal openAmount;
    private boolean isAvaialbelFinancingDocument;

    public MarketShiftPayment() {
        this.account = new Account();
        this.shift = new Shift();
        this.currency = new Currency();
        this.saleType = new Type();
        this.safe = new Safe();
        this.bankAccount = new BankAccount();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getSaleType() {
        return saleType;
    }

    public void setSaleType(Type saleType) {
        this.saleType = saleType;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public BigDecimal getActualSalesPrice() {
        return actualSalesPrice;
    }

    public void setActualSalesPrice(BigDecimal actualSalesPrice) {
        this.actualSalesPrice = actualSalesPrice;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public BigDecimal getOpenAmount() {
        return openAmount;
    }

    public void setOpenAmount(BigDecimal openAmount) {
        this.openAmount = openAmount;
    }

    public boolean isIs_check() {
        return is_check;
    }

    public void setIs_check(boolean is_check) {
        this.is_check = is_check;
    }

    public boolean isIsAvaialbelFinancingDocument() {
        return isAvaialbelFinancingDocument;
    }

    public BigDecimal getInheritedMoney() {
        return inheritedMoney;
    }

    public void setInheritedMoney(BigDecimal inheritedMoney) {
        this.inheritedMoney = inheritedMoney;
    }

    public void setIsAvaialbelFinancingDocument(boolean isAvaialbelFinancingDocument) {
        this.isAvaialbelFinancingDocument = isAvaialbelFinancingDocument;
    }

}

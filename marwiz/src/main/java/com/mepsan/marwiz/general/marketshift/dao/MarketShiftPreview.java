/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 22.02.2019 09:19:30
 */
package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;

public class MarketShiftPreview {

    private int id;
    private String description;
    private Stock stock;
    private Currency currency;
    private Account account;
    private Safe safe;
    private BankAccount bankAccount;
    private Shift shift;

    private BigDecimal saleAmount;
    private BigDecimal returnAmount;
    private BigDecimal salePrice;
    private BigDecimal returnPrice;
    private BigDecimal cashPrice;
    private BigDecimal creditPrice;
    private BigDecimal creditCardPrice;
    private BigDecimal openPrice;
    private BigDecimal taxRate;
    private BigDecimal totalTaxPrice;
    private BigDecimal unitPrice;
    private BigDecimal exchangeRate;
    private BigDecimal exchangePrice;

    private BigDecimal inComing;
    private BigDecimal outGoing;
    private BigDecimal employeInComing;
    private BigDecimal employeOutGoing;
    private BigDecimal totalOfInComing;
    private BigDecimal totalOfOutGoing;
    private BigDecimal previousAmount;
    private BigDecimal previousPrice;
    private BigDecimal previousSaleAmount;
    private BigDecimal previousSalePrice;
    private BigDecimal remainingQuantity;
    private BigDecimal remainingPrice;

    private int categoryId;

    public MarketShiftPreview() {
        this.stock = new Stock();
        this.currency = new Currency();
        this.account = new Account();
        this.safe = new Safe();
        this.bankAccount = new BankAccount();
        this.shift = new Shift();
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

    public BigDecimal getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(BigDecimal saleAmount) {
        this.saleAmount = saleAmount;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getReturnPrice() {
        return returnPrice;
    }

    public void setReturnPrice(BigDecimal returnPrice) {
        this.returnPrice = returnPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTotalTaxPrice() {
        return totalTaxPrice;
    }

    public void setTotalTaxPrice(BigDecimal totalTaxPrice) {
        this.totalTaxPrice = totalTaxPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getInComing() {
        return inComing;
    }

    public void setInComing(BigDecimal inComing) {
        this.inComing = inComing;
    }

    public BigDecimal getOutGoing() {
        return outGoing;
    }

    public void setOutGoing(BigDecimal outGoing) {
        this.outGoing = outGoing;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BigDecimal getExchangePrice() {
        return exchangePrice;
    }

    public void setExchangePrice(BigDecimal exchangePrice) {
        this.exchangePrice = exchangePrice;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public BigDecimal getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(BigDecimal cashPrice) {
        this.cashPrice = cashPrice;
    }

    public BigDecimal getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(BigDecimal creditPrice) {
        this.creditPrice = creditPrice;
    }

    public BigDecimal getCreditCardPrice() {
        return creditCardPrice;
    }

    public void setCreditCardPrice(BigDecimal creditCardPrice) {
        this.creditCardPrice = creditCardPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getEmployeInComing() {
        return employeInComing;
    }

    public void setEmployeInComing(BigDecimal employeInComing) {
        this.employeInComing = employeInComing;
    }

    public BigDecimal getEmployeOutGoing() {
        return employeOutGoing;
    }

    public void setEmployeOutGoing(BigDecimal employeOutGoing) {
        this.employeOutGoing = employeOutGoing;
    }

    public BigDecimal getTotalOfInComing() {
        return totalOfInComing;
    }

    public void setTotalOfInComing(BigDecimal totalOfInComing) {
        this.totalOfInComing = totalOfInComing;
    }

    public BigDecimal getTotalOfOutGoing() {
        return totalOfOutGoing;
    }

    public void setTotalOfOutGoing(BigDecimal totalOfOutGoing) {
        this.totalOfOutGoing = totalOfOutGoing;
    }

    public BigDecimal getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(BigDecimal previousAmount) {
        this.previousAmount = previousAmount;
    }

    public BigDecimal getPreviousSaleAmount() {
        return previousSaleAmount;
    }

    public void setPreviousSaleAmount(BigDecimal previousSaleAmount) {
        this.previousSaleAmount = previousSaleAmount;
    }

    public BigDecimal getPreviousSalePrice() {
        return previousSalePrice;
    }

    public void setPreviousSalePrice(BigDecimal previousSalePrice) {
        this.previousSalePrice = previousSalePrice;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(BigDecimal previousPrice) {
        this.previousPrice = previousPrice;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public BigDecimal getRemainingPrice() {
        return remainingPrice;
    }

    public void setRemainingPrice(BigDecimal remainingPrice) {
        this.remainingPrice = remainingPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}

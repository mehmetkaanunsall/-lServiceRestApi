/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 09:33:33
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class SalePayment extends WotLogging {

    private int id;
    private Sales sales;
    private Type type;
    private BankAccount bankAccount;
    private Safe safe;
    private boolean isDirection;
    private BigDecimal price;
    private Currency currency;
    private BigDecimal exchangeRate;

    private UserData user;///veritabanında yok vardiya raporları için eklendi
    private int saleCount;

    public SalePayment() {
        this.sales = new Sales();
        this.type = new Type();
        this.bankAccount = new BankAccount();
        this.safe = new Safe();
        this.currency = new Currency();
        this.user = new UserData();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    @Override
    public String toString() {
        return this.getSales().getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

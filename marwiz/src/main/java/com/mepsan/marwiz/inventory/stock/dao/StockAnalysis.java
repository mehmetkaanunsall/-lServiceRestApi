/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 15:41:48
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;

public class StockAnalysis {

    private BigDecimal lastDay;
    private BigDecimal lastWeek;
    private BigDecimal lastMonth;
    private BigDecimal lastSalePrice;
    private Currency lastSaleCurrency;
    private BigDecimal lastPurchasePrice;
    private Currency lastPurchaseCurrency;

    private int year;
    private int month;
    private int day;

    public StockAnalysis() {
        this.lastPurchaseCurrency=new Currency();
        this.lastSaleCurrency=new Currency();
    }      

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getLastDay() {
        return lastDay;
    }

    public void setLastDay(BigDecimal lastDay) {
        this.lastDay = lastDay;
    }

    public BigDecimal getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(BigDecimal lastWeek) {
        this.lastWeek = lastWeek;
    }

    public BigDecimal getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(BigDecimal lastMonth) {
        this.lastMonth = lastMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public BigDecimal getLastPurchasePrice() {
        return lastPurchasePrice;
    }

    public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
        this.lastPurchasePrice = lastPurchasePrice;
    }

    public Currency getLastSaleCurrency() {
        return lastSaleCurrency;
    }

    public void setLastSaleCurrency(Currency lastSaleCurrency) {
        this.lastSaleCurrency = lastSaleCurrency;
    }

    public Currency getLastPurchaseCurrency() {
        return lastPurchaseCurrency;
    }

    public void setLastPurchaseCurrency(Currency lastPurchaseCurrency) {
        this.lastPurchaseCurrency = lastPurchaseCurrency;
    }

}

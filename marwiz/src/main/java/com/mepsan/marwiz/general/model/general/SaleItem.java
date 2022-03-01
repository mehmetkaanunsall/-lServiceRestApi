/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 09:38:50
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class SaleItem extends WotLogging {

    private int id;
    private Sales sales;
    private Stock stock;
    private Date processDate;
    private Unit unit;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalPrice;
    private BigDecimal taxRate;
    private BigDecimal totalTax;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private BigDecimal recommendedPrice;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal totalMoney;
    private boolean isManagerDiscount;
    private UserData managerUserData;
    private String category;

    public SaleItem() {
        this.sales = new Sales();
        this.stock = new Stock();
        this.unit = new Unit();
        this.currency = new Currency();
        this.managerUserData = new UserData();
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isIsManagerDiscount() {
        return isManagerDiscount;
    }

    public void setIsManagerDiscount(boolean isManagerDiscount) {
        this.isManagerDiscount = isManagerDiscount;
    }

    public UserData getManagerUserData() {
        return managerUserData;
    }

    public void setManagerUserData(UserData managerUserData) {
        this.managerUserData = managerUserData;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

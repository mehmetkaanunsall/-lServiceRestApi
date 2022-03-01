/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

/**
 *
 * @author esra.cabuk
 */
public class OrderItem extends WotLogging {

    private int id;
    private Order order;
    private Stock stock;
    private Unit unit;
    private BigDecimal boxQuantity;
    private BigDecimal shelfQuantity;
    private BigDecimal minFactorValue;
    private BigDecimal maxFactorValue;
    private BigDecimal warehouseQuantity;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    private BigDecimal quantity;
    private BigDecimal recommendedPrice;
    private Currency currency;
    private String description;

    private BigDecimal requiredWarehouseStock;
    private BigDecimal requiredTotalStock;
    private BigDecimal lastTwoMonthsSales;
    private BigDecimal averageWeeklyOrderQuantity;
    private BigDecimal stockEnoughDay;
    private BigDecimal orderCalculationSupplement;
    private BigDecimal warehouseStockDivisorValue;
    private int twoMonthSaleActiveDay;
    private int averageWeeklyOrderQuantityForDaysCount;

    private BigDecimal remainingQuantity;

    private TaxGroup taxGroup;

    private boolean isInvoice;
    private boolean isCheckInvoice;

    private boolean isCalc; //hesaplama değerleri var ise 1 yoksa 0 set edilir. 0 ise manuel eklenmiş demektir.
    private boolean isNewStockControl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(BigDecimal boxQuantity) {
        this.boxQuantity = boxQuantity;
    }

    public BigDecimal getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(BigDecimal shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public BigDecimal getMinFactorValue() {
        return minFactorValue;
    }

    public void setMinFactorValue(BigDecimal minFactorValue) {
        this.minFactorValue = minFactorValue;
    }

    public BigDecimal getMaxFactorValue() {
        return maxFactorValue;
    }

    public void setMaxFactorValue(BigDecimal maxFactorValue) {
        this.maxFactorValue = maxFactorValue;
    }

    public BigDecimal getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public void setWarehouseQuantity(BigDecimal warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;
    }

    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getRequiredWarehouseStock() {
        return requiredWarehouseStock;
    }

    public void setRequiredWarehouseStock(BigDecimal requiredWarehouseStock) {
        this.requiredWarehouseStock = requiredWarehouseStock;
    }

    public BigDecimal getRequiredTotalStock() {
        return requiredTotalStock;
    }

    public void setRequiredTotalStock(BigDecimal requiredTotalStock) {
        this.requiredTotalStock = requiredTotalStock;
    }

    public BigDecimal getLastTwoMonthsSales() {
        return lastTwoMonthsSales;
    }

    public void setLastTwoMonthsSales(BigDecimal lastTwoMonthsSales) {
        this.lastTwoMonthsSales = lastTwoMonthsSales;
    }

    public BigDecimal getAverageWeeklyOrderQuantity() {
        return averageWeeklyOrderQuantity;
    }

    public void setAverageWeeklyOrderQuantity(BigDecimal averageWeeklyOrderQuantity) {
        this.averageWeeklyOrderQuantity = averageWeeklyOrderQuantity;
    }

    public BigDecimal getStockEnoughDay() {
        return stockEnoughDay;
    }

    public void setStockEnoughDay(BigDecimal stockEnoughDay) {
        this.stockEnoughDay = stockEnoughDay;
    }

    public BigDecimal getOrderCalculationSupplement() {
        return orderCalculationSupplement;
    }

    public void setOrderCalculationSupplement(BigDecimal orderCalculationSupplement) {
        this.orderCalculationSupplement = orderCalculationSupplement;
    }

    public boolean isIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    public boolean isIsCheckInvoice() {
        return isCheckInvoice;
    }

    public void setIsCheckInvoice(boolean isCheckInvoice) {
        this.isCheckInvoice = isCheckInvoice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsCalc() {
        return isCalc;
    }

    public void setIsCalc(boolean isCalc) {
        this.isCalc = isCalc;
    }

    public BigDecimal getWarehouseStockDivisorValue() {
        return warehouseStockDivisorValue;
    }

    public void setWarehouseStockDivisorValue(BigDecimal warehouseStockDivisorValue) {
        this.warehouseStockDivisorValue = warehouseStockDivisorValue;
    }

    public OrderItem() {
        this.order = new Order();
        this.stock = new Stock();
        this.unit = new Unit();
        this.currency = new Currency();
        this.taxGroup = new TaxGroup();
    }

    public int getTwoMonthSaleActiveDay() {
        return twoMonthSaleActiveDay;
    }

    public void setTwoMonthSaleActiveDay(int twoMonthSaleActiveDay) {
        this.twoMonthSaleActiveDay = twoMonthSaleActiveDay;
    }

    public int getAverageWeeklyOrderQuantityForDaysCount() {
        return averageWeeklyOrderQuantityForDaysCount;
    }

    public void setAverageWeeklyOrderQuantityForDaysCount(int averageWeeklyOrderQuantityForDaysCount) {
        this.averageWeeklyOrderQuantityForDaysCount = averageWeeklyOrderQuantityForDaysCount;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    public boolean isIsNewStockControl() {
        return isNewStockControl;
    }

    public void setIsNewStockControl(boolean isNewStockControl) {
        this.isNewStockControl = isNewStockControl;
    }

}

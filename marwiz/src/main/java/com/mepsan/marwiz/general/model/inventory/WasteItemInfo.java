package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author elif.mart
 */
public class WasteItemInfo extends WotLogging {

    private int id;
    private WasteReason wasteReason;
    private int warehouseMovementId;
    private Date expirationDate;
    private BigDecimal taxRate;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal totalMoney;
    private String description;
    private BigDecimal currentPurchasePrice;
    private Unit unit;
    private BigDecimal alternativeUnitQuantity;

    public WasteItemInfo() {

        this.wasteReason = new WasteReason();
        this.currency = new Currency();
        this.unit = new Unit();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WasteReason getWasteReason() {
        return wasteReason;
    }

    public void setWasteReason(WasteReason wasteReason) {
        this.wasteReason = wasteReason;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWarehouseMovementId() {
        return warehouseMovementId;
    }

    public void setWarehouseMovementId(int warehouseMovementId) {
        this.warehouseMovementId = warehouseMovementId;
    }

    public BigDecimal getCurrentPurchasePrice() {
        return currentPurchasePrice;
    }

    public void setCurrentPurchasePrice(BigDecimal currentPurchasePrice) {
        this.currentPurchasePrice = currentPurchasePrice;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getAlternativeUnitQuantity() {
        return alternativeUnitQuantity;
    }

    public void setAlternativeUnitQuantity(BigDecimal alternativeUnitQuantity) {
        this.alternativeUnitQuantity = alternativeUnitQuantity;
    }

}

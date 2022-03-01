/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author esra.cabuk
 */
public class StockInfo {

    private int id;
    private BigDecimal recommendedPrice;
    private boolean isUpdatePrice;
    private boolean isQuickSale;
    private BigDecimal minStockLevel;
    private Currency currency;
    private BigDecimal minProfitRate;
    private BigDecimal currentPurchasePrice;
    private BigDecimal currentSalePrice;
    private Currency currentPurchaseCurrency;
    private Currency currentSaleCurrency;
    private Date lastSalePriceChangeDate;
    private Date purchaseControlDate;
    private BigDecimal saleCount;
    private BigDecimal purchaseCount;
    private BigDecimal purchaseRecommendedPrice;
    private Currency purchaseCurrency;
    private boolean isFuel;
    private String fuelIntegrationCode;
    private BigDecimal saleMandatoryPrice;
    private Currency saleMandatoryCurrency;
    private BigDecimal weight;
    private BigDecimal mainWeight;
    private Unit weightUnit;
    private Unit mainWeightUnit;
    private BigDecimal maxStockLevel;
    private BigDecimal balance;
    private BigDecimal turnoverPremium;
    private String eInvoiceIntegrationCode;
    private BigDecimal orderDeliverySalePrice;
    private Currency orderDeliverySaleCurrency;

    //-------------veritabanında yok stok sayfası için-------------------
    private BigDecimal currentPurchasePriceWithKdv;
    private BigDecimal currentSalePriceWithoutKdv;
    private BigDecimal availablePurchasePriceWithKdv;
    private BigDecimal availablePurchasePriceWithoutKdv;
    private BigDecimal availableSalePriceWithKdv;
    private BigDecimal availableSalePriceWithoutKdv;

    private BigDecimal tempCurrentSalePrice;
    private boolean isMinusStockLevel;
    private Boolean isMinusStockLevelTemp;
    private Boolean isQuickSaleTemp;
    private TaxDepartment taxDepartment;

    private BigDecimal shelfQuantity;
    private BigDecimal stockEnoughDay;

    private BigDecimal minFactorValue;
    private BigDecimal maxFactorValue;
    private BigDecimal warehouseStockDivisorValue;

    private IncomeExpense incomeExpense;
    private boolean isPassive;
    private boolean isDelist;
    private Boolean isCampaign;
    private BigDecimal orderDeliveryRate;

    public StockInfo() {
        this.currency = new Currency();
        this.currentPurchaseCurrency = new Currency();
        this.currentSaleCurrency = new Currency();
        this.purchaseCurrency = new Currency();
        this.saleMandatoryCurrency = new Currency();
        this.weightUnit = new Unit();
        this.mainWeightUnit = new Unit();
        this.taxDepartment = new TaxDepartment();
        this.incomeExpense = new IncomeExpense();
        this.orderDeliverySaleCurrency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public boolean isIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(boolean isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
    }

    public boolean isIsQuickSale() {
        return isQuickSale;
    }

    public void setIsQuickSale(boolean isQuickSale) {
        this.isQuickSale = isQuickSale;
    }

    public BigDecimal getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(BigDecimal minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getMinProfitRate() {
        return minProfitRate;
    }

    public void setMinProfitRate(BigDecimal minProfitRate) {
        this.minProfitRate = minProfitRate;
    }

    public BigDecimal getCurrentPurchasePrice() {
        return currentPurchasePrice;
    }

    public void setCurrentPurchasePrice(BigDecimal currentPurchasePrice) {
        this.currentPurchasePrice = currentPurchasePrice;
    }

    public Currency getCurrentPurchaseCurrency() {
        return currentPurchaseCurrency;
    }

    public void setCurrentPurchaseCurrency(Currency currentPurchaseCurrency) {
        this.currentPurchaseCurrency = currentPurchaseCurrency;
    }

    public Currency getCurrentSaleCurrency() {
        return currentSaleCurrency;
    }

    public void setCurrentSaleCurrency(Currency currentSaleCurrency) {
        this.currentSaleCurrency = currentSaleCurrency;
    }

    public BigDecimal getCurrentSalePrice() {
        return currentSalePrice;
    }

    public void setCurrentSalePrice(BigDecimal currentSalePrice) {
        this.currentSalePrice = currentSalePrice;
    }

    public Date getLastSalePriceChangeDate() {
        return lastSalePriceChangeDate;
    }

    public void setLastSalePriceChangeDate(Date lastSalePriceChangeDate) {
        this.lastSalePriceChangeDate = lastSalePriceChangeDate;
    }

    public Date getPurchaseControlDate() {
        return purchaseControlDate;
    }

    public void setPurchaseControlDate(Date purchaseControlDate) {
        this.purchaseControlDate = purchaseControlDate;
    }

    public BigDecimal getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(BigDecimal saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(BigDecimal purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public BigDecimal getPurchaseRecommendedPrice() {
        return purchaseRecommendedPrice;
    }

    public void setPurchaseRecommendedPrice(BigDecimal purchaseRecommendedPrice) {
        this.purchaseRecommendedPrice = purchaseRecommendedPrice;
    }

    public boolean isIsPassive() {
        return isPassive;
    }

    public void setIsPassive(boolean isPassive) {
        this.isPassive = isPassive;
    }

    public Currency getPurchaseCurrency() {
        return purchaseCurrency;
    }

    public void setPurchaseCurrency(Currency purchaseCurrency) {
        this.purchaseCurrency = purchaseCurrency;
    }

    public boolean isIsFuel() {
        return isFuel;
    }

    public void setIsFuel(boolean isFuel) {
        this.isFuel = isFuel;
    }

    public String getFuelIntegrationCode() {
        return fuelIntegrationCode;
    }

    public void setFuelIntegrationCode(String fuelIntegrationCode) {
        this.fuelIntegrationCode = fuelIntegrationCode;
    }

    public BigDecimal getCurrentPurchasePriceWithKdv() {
        return currentPurchasePriceWithKdv;
    }

    public void setCurrentPurchasePriceWithKdv(BigDecimal currentPurchasePriceWithKdv) {
        this.currentPurchasePriceWithKdv = currentPurchasePriceWithKdv;
    }

    public BigDecimal getCurrentSalePriceWithoutKdv() {
        return currentSalePriceWithoutKdv;
    }

    public void setCurrentSalePriceWithoutKdv(BigDecimal currentSalePriceWithoutKdv) {
        this.currentSalePriceWithoutKdv = currentSalePriceWithoutKdv;
    }

    public BigDecimal getAvailablePurchasePriceWithKdv() {
        return availablePurchasePriceWithKdv;
    }

    public void setAvailablePurchasePriceWithKdv(BigDecimal availablePurchasePriceWithKdv) {
        this.availablePurchasePriceWithKdv = availablePurchasePriceWithKdv;
    }

    public BigDecimal getAvailablePurchasePriceWithoutKdv() {
        return availablePurchasePriceWithoutKdv;
    }

    public void setAvailablePurchasePriceWithoutKdv(BigDecimal availablePurchasePriceWithoutKdv) {
        this.availablePurchasePriceWithoutKdv = availablePurchasePriceWithoutKdv;
    }

    public BigDecimal getAvailableSalePriceWithKdv() {
        return availableSalePriceWithKdv;
    }

    public void setAvailableSalePriceWithKdv(BigDecimal availableSalePriceWithKdv) {
        this.availableSalePriceWithKdv = availableSalePriceWithKdv;
    }

    public BigDecimal getAvailableSalePriceWithoutKdv() {
        return availableSalePriceWithoutKdv;
    }

    public void setAvailableSalePriceWithoutKdv(BigDecimal availableSalePriceWithoutKdv) {
        this.availableSalePriceWithoutKdv = availableSalePriceWithoutKdv;
    }

    public BigDecimal getSaleMandatoryPrice() {
        return saleMandatoryPrice;
    }

    public void setSaleMandatoryPrice(BigDecimal saleMandatoryPrice) {
        this.saleMandatoryPrice = saleMandatoryPrice;
    }

    public Currency getSaleMandatoryCurrency() {
        return saleMandatoryCurrency;
    }

    public void setSaleMandatoryCurrency(Currency saleMandatoryCurrency) {
        this.saleMandatoryCurrency = saleMandatoryCurrency;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getMainWeight() {
        return mainWeight;
    }

    public void setMainWeight(BigDecimal mainWeight) {
        this.mainWeight = mainWeight;
    }

    public Unit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(Unit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Unit getMainWeightUnit() {
        return mainWeightUnit;
    }

    public void setMainWeightUnit(Unit mainWeightUnit) {
        this.mainWeightUnit = mainWeightUnit;
    }

    public BigDecimal getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(BigDecimal maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTempCurrentSalePrice() {
        return tempCurrentSalePrice;
    }

    public void setTempCurrentSalePrice(BigDecimal tempCurrentSalePrice) {
        this.tempCurrentSalePrice = tempCurrentSalePrice;
    }

    public boolean isIsMinusStockLevel() {
        return isMinusStockLevel;
    }

    public void setIsMinusStockLevel(boolean isMinusStockLevel) {
        this.isMinusStockLevel = isMinusStockLevel;
    }

    public Boolean getIsMinusStockLevelTemp() {
        return isMinusStockLevelTemp;
    }

    public void setIsMinusStockLevelTemp(Boolean isMinusStockLevelTemp) {
        this.isMinusStockLevelTemp = isMinusStockLevelTemp;
    }

    public Boolean getIsQuickSaleTemp() {
        return isQuickSaleTemp;
    }

    public void setIsQuickSaleTemp(Boolean isQuickSaleTemp) {
        this.isQuickSaleTemp = isQuickSaleTemp;
    }

    public TaxDepartment getTaxDepartment() {
        return taxDepartment;
    }

    public void setTaxDepartment(TaxDepartment taxDepartment) {
        this.taxDepartment = taxDepartment;
    }

    public BigDecimal getTurnoverPremium() {
        return turnoverPremium;
    }

    public void setTurnoverPremium(BigDecimal turnoverPremium) {
        this.turnoverPremium = turnoverPremium;
    }

    public BigDecimal getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(BigDecimal shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public BigDecimal getStockEnoughDay() {
        return stockEnoughDay;
    }

    public void setStockEnoughDay(BigDecimal stockEnoughDay) {
        this.stockEnoughDay = stockEnoughDay;
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

    public BigDecimal getWarehouseStockDivisorValue() {
        return warehouseStockDivisorValue;
    }

    public void setWarehouseStockDivisorValue(BigDecimal warehouseStockDivisorValue) {
        this.warehouseStockDivisorValue = warehouseStockDivisorValue;
    }

    public IncomeExpense getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public String geteInvoiceIntegrationCode() {
        return eInvoiceIntegrationCode;
    }

    public void seteInvoiceIntegrationCode(String eInvoiceIntegrationCode) {
        this.eInvoiceIntegrationCode = eInvoiceIntegrationCode;
    }

    public boolean isIsDelist() {
        return isDelist;
    }

    public void setIsDelist(boolean isDelist) {
        this.isDelist = isDelist;
    }

    public Boolean getIsCampaign() {
        return isCampaign;
    }

    public void setIsCampaign(Boolean isCampaign) {
        this.isCampaign = isCampaign;
    }

    public BigDecimal getOrderDeliveryRate() {
        return orderDeliveryRate;
    }

    public void setOrderDeliveryRate(BigDecimal orderDeliveryRate) {
        this.orderDeliveryRate = orderDeliveryRate;
    }

    public BigDecimal getOrderDeliverySalePrice() {
        return orderDeliverySalePrice;
    }

    public void setOrderDeliverySalePrice(BigDecimal orderDeliverySalePrice) {
        this.orderDeliverySalePrice = orderDeliverySalePrice;
    }

    public Currency getOrderDeliverySaleCurrency() {
        return orderDeliverySaleCurrency;
    }

    public void setOrderDeliverySaleCurrency(Currency orderDeliverySaleCurrency) {
        this.orderDeliverySaleCurrency = orderDeliverySaleCurrency;
    }
    
    

}

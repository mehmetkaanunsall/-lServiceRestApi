/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.xmlbeans.impl.inst2xsd.VenetianBlindStrategy;

/**
 *
 * @author m.duzoylum
 */
public class GeneralStation extends WotLogging {

    private int id;
    private Date processDate;
    private BigDecimal transferQuantity;
    private BigDecimal transferAmount;
    private BigDecimal purchaseQuantity;
    private BigDecimal purchaseAmount;
    private BigDecimal salesQuantity;
    private BigDecimal salesAmount;
    private BigDecimal remainingQuantity;
    private BigDecimal remainingAmount;
    private BigDecimal rateofProfit;
    private BigDecimal profitMargin;
    private BigDecimal profitAmount;

    private Currency saleCurrencyId;
    private Currency purchaseCurrencyid;
    private Stock stock;
    private BranchSetting branchSetting;
    private Categorization categorization;
    private int count;
    private String automatName;
    private BigDecimal cost;
    private AutomationDevice vendingMachine;

    //Yıkama makinesi satışları için tutuldu
    private BigDecimal electricQuantity;
    private BigDecimal electricOperationTime;
    private BigDecimal electricExpense;
    private BigDecimal totalElectricAmount;
    private BigDecimal waterWorkingAmount;
    private int waterWorkingTime;
    private BigDecimal waterExpense;
    private BigDecimal waterWaste;
    private BigDecimal waste;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalWinnings;
    private int saleType;
    private int operationTime;

    public GeneralStation() {
        this.stock = new Stock();
        this.branchSetting = new BranchSetting();
        this.saleCurrencyId = new Currency();
        this.purchaseCurrencyid = new Currency();
        this.categorization = new Categorization();
        this.vendingMachine = new AutomationDevice();
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public BigDecimal getTransferQuantity() {
        return transferQuantity;
    }

    public void setTransferQuantity(BigDecimal transferQuantity) {
        this.transferQuantity = transferQuantity;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(BigDecimal purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public BigDecimal getSalesQuantity() {
        return salesQuantity;
    }

    public void setSalesQuantity(BigDecimal salesQuantity) {
        this.salesQuantity = salesQuantity;
    }

    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getRateofProfit() {
        return rateofProfit;
    }

    public void setRateofProfit(BigDecimal rateofProfit) {
        this.rateofProfit = rateofProfit;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public Currency getSaleCurrencyId() {
        return saleCurrencyId;
    }

    public void setSaleCurrencyId(Currency saleCurrencyId) {
        this.saleCurrencyId = saleCurrencyId;
    }

    public Currency getPurchaseCurrencyid() {
        return purchaseCurrencyid;
    }

    public void setPurchaseCurrencyid(Currency purchaseCurrencyid) {
        this.purchaseCurrencyid = purchaseCurrencyid;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAutomatName() {
        return automatName;
    }

    public void setAutomatName(String automatName) {
        this.automatName = automatName;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public AutomationDevice getVendingMachine() {
        return vendingMachine;
    }

    public void setVendingMachine(AutomationDevice vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public BigDecimal getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(BigDecimal electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public BigDecimal getElectricOperationTime() {
        return electricOperationTime;
    }

    public void setElectricOperationTime(BigDecimal electricOperationTime) {
        this.electricOperationTime = electricOperationTime;
    }

    public BigDecimal getElectricExpense() {
        return electricExpense;
    }

    public void setElectricExpense(BigDecimal electricExpense) {
        this.electricExpense = electricExpense;
    }

    public BigDecimal getTotalElectricAmount() {
        return totalElectricAmount;
    }

    public void setTotalElectricAmount(BigDecimal totalElectricAmount) {
        this.totalElectricAmount = totalElectricAmount;
    }

    public BigDecimal getWaterWorkingAmount() {
        return waterWorkingAmount;
    }

    public void setWaterWorkingAmount(BigDecimal waterWorkingAmount) {
        this.waterWorkingAmount = waterWorkingAmount;
    }

    public int getWaterWorkingTime() {
        return waterWorkingTime;
    }

    public void setWaterWorkingTime(int waterWorkingTime) {
        this.waterWorkingTime = waterWorkingTime;
    }

    public BigDecimal getWaterExpense() {
        return waterExpense;
    }

    public void setWaterExpense(BigDecimal waterExpense) {
        this.waterExpense = waterExpense;
    }

    public BigDecimal getWaterWaste() {
        return waterWaste;
    }

    public void setWaterWaste(BigDecimal waterWaste) {
        this.waterWaste = waterWaste;
    }

    public BigDecimal getWaste() {
        return waste;
    }

    public void setWaste(BigDecimal waste) {
        this.waste = waste;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(BigDecimal totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public int getSaleType() {
        return saleType;
    }

    public void setSaleType(int saleType) {
        this.saleType = saleType;
    }

    public int getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(int operationTime) {
        this.operationTime = operationTime;
    }

}

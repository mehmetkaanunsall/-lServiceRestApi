/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:22:45 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

public class StationSalesSummaryReport {

    private Date beginDate;
    private Date endDate;
    private String fuelStockName;
    private BigDecimal fuelStockQuantity;
    private BigDecimal fuelStockSalesTotal;
    private BigDecimal fuelStockUnitPrice;
    private String fuelCollectionName;
    private BigDecimal fuelCollectionSalesTotal;
    private BigDecimal moreMoney;
    private BigDecimal incomeExpenseMoney;
    private String salesTypeName;
    private BigDecimal marketSalesQuantity;
    private BigDecimal marketSaleTotalMoney;
    private String marketCollectionTypeName;
    private BigDecimal marketCollectionTotalMoney;
    private BigDecimal marketDeptMoney;
    private String totalSalesName;
    private BigDecimal totalSalesPrice;
    private String totalCollectionName;
    private BigDecimal totalCollectionPrice;
    private Unit stockUnit;
    private BigDecimal marketSalesDept;
    private BigDecimal fuelSalesDept;
    private int deptType;
    private int fuelSaleTypeId;
    private BranchSetting branchSetting;
    private String branchName;
    private Currency currency;

    private Currency fuelSalesCurreny;
    private BigDecimal accountCollectionPaymentMoney;

    public StationSalesSummaryReport() {
        stockUnit = new Unit();
        branchSetting = new BranchSetting();
        currency = new Currency();
        fuelSalesCurreny = new Currency();
    }

    public Currency getFuelSalesCurreny() {
        return fuelSalesCurreny;
    }

    public void setFuelSalesCurreny(Currency fuelSalesCurreny) {
        this.fuelSalesCurreny = fuelSalesCurreny;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFuelStockName() {
        return fuelStockName;
    }

    public void setFuelStockName(String fuelStockName) {
        this.fuelStockName = fuelStockName;
    }

    public BigDecimal getFuelStockQuantity() {
        return fuelStockQuantity;
    }

    public void setFuelStockQuantity(BigDecimal fuelStockQuantity) {
        this.fuelStockQuantity = fuelStockQuantity;
    }

    public BigDecimal getFuelStockSalesTotal() {
        return fuelStockSalesTotal;
    }

    public void setFuelStockSalesTotal(BigDecimal fuelStockSalesTotal) {
        this.fuelStockSalesTotal = fuelStockSalesTotal;
    }

    public String getFuelCollectionName() {
        return fuelCollectionName;
    }

    public void setFuelCollectionName(String fuelCollectionName) {
        this.fuelCollectionName = fuelCollectionName;
    }

    public BigDecimal getFuelCollectionSalesTotal() {
        return fuelCollectionSalesTotal;
    }

    public void setFuelCollectionSalesTotal(BigDecimal fuelCollectionSalesTotal) {
        this.fuelCollectionSalesTotal = fuelCollectionSalesTotal;
    }

    public BigDecimal getMoreMoney() {
        return moreMoney;
    }

    public void setMoreMoney(BigDecimal moreMoney) {
        this.moreMoney = moreMoney;
    }

    public BigDecimal getIncomeExpenseMoney() {
        return incomeExpenseMoney;
    }

    public void setIncomeExpenseMoney(BigDecimal incomeExpenseMoney) {
        this.incomeExpenseMoney = incomeExpenseMoney;
    }

    public BigDecimal getFuelStockUnitPrice() {
        return fuelStockUnitPrice;
    }

    public void setFuelStockUnitPrice(BigDecimal fuelStockUnitPrice) {
        this.fuelStockUnitPrice = fuelStockUnitPrice;
    }

    public String getSalesTypeName() {
        return salesTypeName;
    }

    public void setSalesTypeName(String salesTypeName) {
        this.salesTypeName = salesTypeName;
    }

    public BigDecimal getMarketSalesQuantity() {
        return marketSalesQuantity;
    }

    public void setMarketSalesQuantity(BigDecimal marketSalesQuantity) {
        this.marketSalesQuantity = marketSalesQuantity;
    }

    public BigDecimal getMarketSaleTotalMoney() {
        return marketSaleTotalMoney;
    }

    public void setMarketSaleTotalMoney(BigDecimal marketSaleTotalMoney) {
        this.marketSaleTotalMoney = marketSaleTotalMoney;
    }

    public String getMarketCollectionTypeName() {
        return marketCollectionTypeName;
    }

    public void setMarketCollectionTypeName(String marketCollectionTypeName) {
        this.marketCollectionTypeName = marketCollectionTypeName;
    }

    public BigDecimal getMarketCollectionTotalMoney() {
        return marketCollectionTotalMoney;
    }

    public void setMarketCollectionTotalMoney(BigDecimal marketCollectionTotalMoney) {
        this.marketCollectionTotalMoney = marketCollectionTotalMoney;
    }

    public BigDecimal getMarketDeptMoney() {
        return marketDeptMoney;
    }

    public void setMarketDeptMoney(BigDecimal marketDeptMoney) {
        this.marketDeptMoney = marketDeptMoney;
    }

    public String getTotalSalesName() {
        return totalSalesName;
    }

    public void setTotalSalesName(String totalSalesName) {
        this.totalSalesName = totalSalesName;
    }

    public BigDecimal getTotalSalesPrice() {
        return totalSalesPrice;
    }

    public void setTotalSalesPrice(BigDecimal totalSalesPrice) {
        this.totalSalesPrice = totalSalesPrice;
    }

    public String getTotalCollectionName() {
        return totalCollectionName;
    }

    public void setTotalCollectionName(String totalCollectionName) {
        this.totalCollectionName = totalCollectionName;
    }

    public BigDecimal getTotalCollectionPrice() {
        return totalCollectionPrice;
    }

    public void setTotalCollectionPrice(BigDecimal totalCollectionPrice) {
        this.totalCollectionPrice = totalCollectionPrice;
    }

    public Unit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(Unit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public BigDecimal getMarketSalesDept() {
        return marketSalesDept;
    }

    public void setMarketSalesDept(BigDecimal marketSalesDept) {
        this.marketSalesDept = marketSalesDept;
    }

    public BigDecimal getFuelSalesDept() {
        return fuelSalesDept;
    }

    public void setFuelSalesDept(BigDecimal fuelSalesDept) {
        this.fuelSalesDept = fuelSalesDept;
    }

    public int getDeptType() {
        return deptType;
    }

    public void setDeptType(int deptType) {
        this.deptType = deptType;
    }

    public int getFuelSaleTypeId() {
        return fuelSaleTypeId;
    }

    public void setFuelSaleTypeId(int fuelSaleTypeId) {
        this.fuelSaleTypeId = fuelSaleTypeId;
    }

    public BigDecimal getAccountCollectionPaymentMoney() {
        return accountCollectionPaymentMoney;
    }

    public void setAccountCollectionPaymentMoney(BigDecimal accountCollectionPaymentMoney) {
        this.accountCollectionPaymentMoney = accountCollectionPaymentMoney;
    }

}

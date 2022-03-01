/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 12:07:33
 */
package com.mepsan.marwiz.general.report.profitmarginreport.dao;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfitMarginReport extends WotLogging {

    private int id;
    private Date beginDate;
    private Date endDate;
    private Stock stock;
    private BigDecimal quantity;
    private List<Stock> stockList;
    private BigDecimal totalPurchasePrice;
    private BigDecimal totalSalesPrice;
    private boolean calculationType;
    private BigDecimal purchaseQuantity;
    private boolean reportType;
    private List<Categorization> listOfCategorization;
    private Categorization categorization;
    private boolean isTaxIncluded;
    private boolean isAllStock;

    private BigDecimal warehouseStartQuantity;
    private BigDecimal warehouseEndQuantity;
    private BigDecimal beginToEndPurchaseQuantity;
    private BigDecimal beginToEndPurchasePrice;
    private BigDecimal beginToEndSalesQuantity;
    private BigDecimal beginToEndSalesPrice;

    private BigDecimal warehouseStartPrice;
    private BigDecimal warehouseEndPrice;

    private BigDecimal stockTakingPrice;
    private BigDecimal stockTakingQuantity;

    private BigDecimal overallStockTakingPrice;
    private BigDecimal overallStockTakingQuantity;

    private BigDecimal overallTotalPurchase;
    private BigDecimal overallTotalSales;

    private BigDecimal overallTotalWarehouseStartPrice;
    private BigDecimal overallTotalWarehouseEndPrice;
    private BigDecimal overallWarehouseStartQuantity;
    private BigDecimal overallWarehouseEndQuantity;
    private BigDecimal overallQuantity;
    private BigDecimal overallBeginToEndPurchasePrice;
    private BigDecimal overallBeginToEndSalesPrice;
    private BigDecimal overallBeginToEndPurchaseQuantity;
    private BigDecimal overallBeginToEndSalesQuantity;
    private BigDecimal profitMargin;
    private BigDecimal profitPercentage;
    private BigDecimal totalProfit;
    private BigDecimal overallPurchaseQuantity;
    private BigDecimal overallDifferencePrice;
    private BigDecimal overallZSalesQuantity;
    private BigDecimal overallZSalesPrice;

    private List<BranchSetting> selectedBranchList;
    private BranchSetting branchSetting;
    private Currency currency;
    private boolean isCalculateStockTaking;
    private BigDecimal tempOverallTotalSales;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;

    private BigDecimal beginToEndPurchaseReturnQuantity;
    private BigDecimal beginToEndPurchaseReturnPrice;
    private BigDecimal overallBeginToEndPurchaseReturnQuantity;
    private BigDecimal overallBeginToEndPurchaseReturnPrice;
    private BigDecimal differencePrice;

    private boolean isExcludingServiceStock;
    private BigDecimal zSalesQuantity;
    private BigDecimal zSalesPrice;
    private BigDecimal zSalesQuantityExcluding;
    private BigDecimal zSalesPriceExcluding;

    public ProfitMarginReport() {
        this.stock = new Stock();
        this.stockList = new ArrayList<>();
        this.listOfCategorization = new ArrayList<>();
        this.categorization = new Categorization();
        this.selectedBranchList = new ArrayList<>();
        this.branchSetting = new BranchSetting();
        this.currency = new Currency();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPurchasePrice() {
        return totalPurchasePrice;
    }

    public void setTotalPurchasePrice(BigDecimal totalPurchasePrice) {
        this.totalPurchasePrice = totalPurchasePrice;
    }

    public BigDecimal getTotalSalesPrice() {
        return totalSalesPrice;
    }

    public void setTotalSalesPrice(BigDecimal totalSalesPrice) {
        this.totalSalesPrice = totalSalesPrice;
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public boolean isCalculationType() {
        return calculationType;
    }

    public void setCalculationType(boolean calculationType) {
        this.calculationType = calculationType;
    }

    public BigDecimal getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(BigDecimal purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public boolean isReportType() {
        return reportType;
    }

    public void setReportType(boolean reportType) {
        this.reportType = reportType;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    public BigDecimal getWarehouseStartQuantity() {
        return warehouseStartQuantity;
    }

    public void setWarehouseStartQuantity(BigDecimal warehouseStartQuantity) {
        this.warehouseStartQuantity = warehouseStartQuantity;
    }

    public BigDecimal getWarehouseEndQuantity() {
        return warehouseEndQuantity;
    }

    public void setWarehouseEndQuantity(BigDecimal warehouseEndQuantity) {
        this.warehouseEndQuantity = warehouseEndQuantity;
    }

    public BigDecimal getBeginToEndPurchaseQuantity() {
        return beginToEndPurchaseQuantity;
    }

    public void setBeginToEndPurchaseQuantity(BigDecimal beginToEndPurchaseQuantity) {
        this.beginToEndPurchaseQuantity = beginToEndPurchaseQuantity;
    }

    public BigDecimal getBeginToEndPurchasePrice() {
        return beginToEndPurchasePrice;
    }

    public void setBeginToEndPurchasePrice(BigDecimal beginToEndPurchasePrice) {
        this.beginToEndPurchasePrice = beginToEndPurchasePrice;
    }

    public BigDecimal getBeginToEndSalesQuantity() {
        return beginToEndSalesQuantity;
    }

    public void setBeginToEndSalesQuantity(BigDecimal beginToEndSalesQuantity) {
        this.beginToEndSalesQuantity = beginToEndSalesQuantity;
    }

    public BigDecimal getBeginToEndSalesPrice() {
        return beginToEndSalesPrice;
    }

    public void setBeginToEndSalesPrice(BigDecimal beginToEndSalesPrice) {
        this.beginToEndSalesPrice = beginToEndSalesPrice;
    }

    public boolean isIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public boolean isIsAllStock() {
        return isAllStock;
    }

    public void setIsAllStock(boolean isAllStock) {
        this.isAllStock = isAllStock;
    }

    public BigDecimal getWarehouseStartPrice() {
        return warehouseStartPrice;
    }

    public void setWarehouseStartPrice(BigDecimal warehouseStartPrice) {
        this.warehouseStartPrice = warehouseStartPrice;
    }

    public BigDecimal getWarehouseEndPrice() {
        return warehouseEndPrice;
    }

    public void setWarehouseEndPrice(BigDecimal warehouseEndPrice) {
        this.warehouseEndPrice = warehouseEndPrice;
    }

    public BigDecimal getOverallTotalPurchase() {
        return overallTotalPurchase;
    }

    public void setOverallTotalPurchase(BigDecimal overallTotalPurchase) {
        this.overallTotalPurchase = overallTotalPurchase;
    }

    public BigDecimal getOverallTotalSales() {
        return overallTotalSales;
    }

    public void setOverallTotalSales(BigDecimal overallTotalSales) {
        this.overallTotalSales = overallTotalSales;
    }

    public BigDecimal getOverallTotalWarehouseStartPrice() {
        return overallTotalWarehouseStartPrice;
    }

    public void setOverallTotalWarehouseStartPrice(BigDecimal overallTotalWarehouseStartPrice) {
        this.overallTotalWarehouseStartPrice = overallTotalWarehouseStartPrice;
    }

    public BigDecimal getOverallTotalWarehouseEndPrice() {
        return overallTotalWarehouseEndPrice;
    }

    public void setOverallTotalWarehouseEndPrice(BigDecimal overallTotalWarehouseEndPrice) {
        this.overallTotalWarehouseEndPrice = overallTotalWarehouseEndPrice;
    }

    public BigDecimal getOverallWarehouseStartQuantity() {
        return overallWarehouseStartQuantity;
    }

    public void setOverallWarehouseStartQuantity(BigDecimal overallWarehouseStartQuantity) {
        this.overallWarehouseStartQuantity = overallWarehouseStartQuantity;
    }

    public BigDecimal getOverallWarehouseEndQuantity() {
        return overallWarehouseEndQuantity;
    }

    public void setOverallWarehouseEndQuantity(BigDecimal overallWarehouseEndQuantity) {
        this.overallWarehouseEndQuantity = overallWarehouseEndQuantity;
    }

    public BigDecimal getOverallQuantity() {
        return overallQuantity;
    }

    public void setOverallQuantity(BigDecimal overallQuantity) {
        this.overallQuantity = overallQuantity;
    }

    public BigDecimal getOverallBeginToEndPurchasePrice() {
        return overallBeginToEndPurchasePrice;
    }

    public void setOverallBeginToEndPurchasePrice(BigDecimal overallBeginToEndPurchasePrice) {
        this.overallBeginToEndPurchasePrice = overallBeginToEndPurchasePrice;
    }

    public BigDecimal getOverallBeginToEndSalesPrice() {
        return overallBeginToEndSalesPrice;
    }

    public void setOverallBeginToEndSalesPrice(BigDecimal overallBeginToEndSalesPrice) {
        this.overallBeginToEndSalesPrice = overallBeginToEndSalesPrice;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(BigDecimal profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getOverallPurchaseQuantity() {
        return overallPurchaseQuantity;
    }

    public void setOverallPurchaseQuantity(BigDecimal overallPurchaseQuantity) {
        this.overallPurchaseQuantity = overallPurchaseQuantity;
    }

    public BigDecimal getOverallBeginToEndPurchaseQuantity() {
        return overallBeginToEndPurchaseQuantity;
    }

    public void setOverallBeginToEndPurchaseQuantity(BigDecimal overallBeginToEndPurchaseQuantity) {
        this.overallBeginToEndPurchaseQuantity = overallBeginToEndPurchaseQuantity;
    }

    public BigDecimal getOverallBeginToEndSalesQuantity() {
        return overallBeginToEndSalesQuantity;
    }

    public void setOverallBeginToEndSalesQuantity(BigDecimal overallBeginToEndSalesQuantity) {
        this.overallBeginToEndSalesQuantity = overallBeginToEndSalesQuantity;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getStockTakingPrice() {
        return stockTakingPrice;
    }

    public void setStockTakingPrice(BigDecimal stockTakingPrice) {
        this.stockTakingPrice = stockTakingPrice;
    }

    public BigDecimal getStockTakingQuantity() {
        return stockTakingQuantity;
    }

    public void setStockTakingQuantity(BigDecimal stockTakingQuantity) {
        this.stockTakingQuantity = stockTakingQuantity;
    }

    public BigDecimal getOverallStockTakingPrice() {
        return overallStockTakingPrice;
    }

    public void setOverallStockTakingPrice(BigDecimal overallStockTakingPrice) {
        this.overallStockTakingPrice = overallStockTakingPrice;
    }

    public BigDecimal getOverallStockTakingQuantity() {
        return overallStockTakingQuantity;
    }

    public void setOverallStockTakingQuantity(BigDecimal overallStockTakingQuantity) {
        this.overallStockTakingQuantity = overallStockTakingQuantity;
    }

    public boolean isIsCalculateStockTaking() {
        return isCalculateStockTaking;
    }

    public void setIsCalculateStockTaking(boolean isCalculateStockTaking) {
        this.isCalculateStockTaking = isCalculateStockTaking;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    public BigDecimal getTempOverallTotalSales() {
        return tempOverallTotalSales;
    }

    public BigDecimal getBeginToEndPurchaseReturnQuantity() {
        return beginToEndPurchaseReturnQuantity;
    }

    public void setBeginToEndPurchaseReturnQuantity(BigDecimal beginToEndPurchaseReturnQuantity) {
        this.beginToEndPurchaseReturnQuantity = beginToEndPurchaseReturnQuantity;
    }

    public BigDecimal getBeginToEndPurchaseReturnPrice() {
        return beginToEndPurchaseReturnPrice;
    }

    public void setBeginToEndPurchaseReturnPrice(BigDecimal beginToEndPurchaseReturnPrice) {
        this.beginToEndPurchaseReturnPrice = beginToEndPurchaseReturnPrice;
    }

    public BigDecimal getOverallBeginToEndPurchaseReturnQuantity() {
        return overallBeginToEndPurchaseReturnQuantity;
    }

    public void setOverallBeginToEndPurchaseReturnQuantity(BigDecimal overallBeginToEndPurchaseReturnQuantity) {
        this.overallBeginToEndPurchaseReturnQuantity = overallBeginToEndPurchaseReturnQuantity;
    }

    public BigDecimal getOverallBeginToEndPurchaseReturnPrice() {
        return overallBeginToEndPurchaseReturnPrice;
    }

    public void setOverallBeginToEndPurchaseReturnPrice(BigDecimal overallBeginToEndPurchaseReturnPrice) {
        this.overallBeginToEndPurchaseReturnPrice = overallBeginToEndPurchaseReturnPrice;
    }

    public void setTempOverallTotalSales(BigDecimal tempOverallTotalSales) {
        this.tempOverallTotalSales = tempOverallTotalSales;
    }

    public BigDecimal getOverallDifferencePrice() {
        return overallDifferencePrice;
    }

    public void setOverallDifferencePrice(BigDecimal overallDifferencePrice) {
        this.overallDifferencePrice = overallDifferencePrice;
    }

    public BigDecimal getDifferencePrice() {
        return differencePrice;
    }

    public void setDifferencePrice(BigDecimal differencePrice) {
        this.differencePrice = differencePrice;
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }

    public BigDecimal getzSalesQuantity() {
        return zSalesQuantity;
    }

    public void setzSalesQuantity(BigDecimal zSalesQuantity) {
        this.zSalesQuantity = zSalesQuantity;
    }

    public BigDecimal getzSalesPrice() {
        return zSalesPrice;
    }

    public void setzSalesPrice(BigDecimal zSalesPrice) {
        this.zSalesPrice = zSalesPrice;
    }

    public boolean isIsExcludingServiceStock() {
        return isExcludingServiceStock;
    }

    public void setIsExcludingServiceStock(boolean isExcludingServiceStock) {
        this.isExcludingServiceStock = isExcludingServiceStock;
    }

    public BigDecimal getOverallZSalesPrice() {
        return overallZSalesPrice;
    }

    public void setOverallZSalesPrice(BigDecimal overallZSalesPrice) {
        this.overallZSalesPrice = overallZSalesPrice;
    }

    public BigDecimal getOverallZSalesQuantity() {
        return overallZSalesQuantity;
    }

    public void setOverallZSalesQuantity(BigDecimal overallZSalesQuantity) {
        this.overallZSalesQuantity = overallZSalesQuantity;
    }

    public BigDecimal getzSalesQuantityExcluding() {
        return zSalesQuantityExcluding;
    }

    public void setzSalesQuantityExcluding(BigDecimal zSalesQuantityExcluding) {
        this.zSalesQuantityExcluding = zSalesQuantityExcluding;
    }

    public BigDecimal getzSalesPriceExcluding() {
        return zSalesPriceExcluding;
    }

    public void setzSalesPriceExcluding(BigDecimal zSalesPriceExcluding) {
        this.zSalesPriceExcluding = zSalesPriceExcluding;
    }

}

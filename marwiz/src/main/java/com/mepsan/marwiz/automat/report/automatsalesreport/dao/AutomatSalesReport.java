/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 11:55:01
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
public class AutomatSalesReport extends AutomatSales {

    private Date beginDate;
    private Date endDate;
    private BigDecimal minSalesPrice;
    private BigDecimal maxSalesPrice;
    private int subTotal;
    private Date saleDate;
    private int subTotalCount;
    private BigDecimal subTotalMoney;
    private List<Stock> listOfStock;
    private List<WashingPlatform> listOfPlatform;
    private List<WashingNozzle> listOfNozzle;
    private List<WashingTank> listOfTank;
    private List<String> listOfPaymentType;
    private List<AutomationDevice> listOfVendingMachine;
    private List<WashingMachicne> listOfWashingMachine;

    private BigDecimal quantitiy; // tabloda yok
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalWinnings;
    private BigDecimal electricQuantity;
    private BigDecimal electricOperationTime;
    private BigDecimal electricExpense;
    private BigDecimal waste;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal totalElectricAmount;
    private BigDecimal waterQuantity;
    private BigDecimal waterUnitPrice;
    private BigDecimal waterWorkingAmount;
    private int waterWorkingTime;
    private BigDecimal waterExpense;
    private BigDecimal waterWaste;
    private BigDecimal totalNetIncome;
    private BigDecimal totalNetExpense;
    private BigDecimal netTotal;

    public AutomatSalesReport() {

        this.listOfStock = new ArrayList<>();
        this.listOfNozzle = new ArrayList<>();
        this.listOfPlatform = new ArrayList<>();
        this.listOfTank = new ArrayList<>();
        this.listOfPaymentType = new ArrayList<>();
        this.listOfVendingMachine = new ArrayList<>();
        this.listOfWashingMachine = new ArrayList<>();
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

    public BigDecimal getMinSalesPrice() {
        return minSalesPrice;
    }

    public void setMinSalesPrice(BigDecimal minSalesPrice) {
        this.minSalesPrice = minSalesPrice;
    }

    public BigDecimal getMaxSalesPrice() {
        return maxSalesPrice;
    }

    public void setMaxSalesPrice(BigDecimal maxSalesPrice) {
        this.maxSalesPrice = maxSalesPrice;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public int getSubTotalCount() {
        return subTotalCount;
    }

    public void setSubTotalCount(int subTotalCount) {
        this.subTotalCount = subTotalCount;
    }

    public BigDecimal getSubTotalMoney() {
        return subTotalMoney;
    }

    public void setSubTotalMoney(BigDecimal subTotalMoney) {
        this.subTotalMoney = subTotalMoney;
    }

    public List<WashingPlatform> getListOfPlatform() {
        return listOfPlatform;
    }

    public void setListOfPlatform(List<WashingPlatform> listOfPlatform) {
        this.listOfPlatform = listOfPlatform;
    }

    public List<WashingNozzle> getListOfNozzle() {
        return listOfNozzle;
    }

    public void setListOfNozzle(List<WashingNozzle> listOfNozzle) {
        this.listOfNozzle = listOfNozzle;
    }

    public List<WashingTank> getListOfTank() {
        return listOfTank;
    }

    public void setListOfTank(List<WashingTank> listOfTank) {
        this.listOfTank = listOfTank;
    }

    public List<String> getListOfPaymentType() {
        return listOfPaymentType;
    }

    public void setListOfPaymentType(List<String> listOfPaymentType) {
        this.listOfPaymentType = listOfPaymentType;
    }

    public List<AutomationDevice> getListOfVendingMachine() {
        return listOfVendingMachine;
    }

    public void setListOfVendingMachine(List<AutomationDevice> listOfVendingMachine) {
        this.listOfVendingMachine = listOfVendingMachine;
    }

    public List<WashingMachicne> getListOfWashingMachine() {
        return listOfWashingMachine;
    }

    public void setListOfWashingMachine(List<WashingMachicne> listOfWashingMachine) {
        this.listOfWashingMachine = listOfWashingMachine;
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

    public BigDecimal getWaste() {
        return waste;
    }

    public void setWaste(BigDecimal waste) {
        this.waste = waste;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getTotalElectricAmount() {
        return totalElectricAmount;
    }

    public void setTotalElectricAmount(BigDecimal totalElectricAmount) {
        this.totalElectricAmount = totalElectricAmount;
    }

    public BigDecimal getWaterQuantity() {
        return waterQuantity;
    }

    public void setWaterQuantity(BigDecimal waterQuantity) {
        this.waterQuantity = waterQuantity;
    }

    public BigDecimal getWaterUnitPrice() {
        return waterUnitPrice;
    }

    public void setWaterUnitPrice(BigDecimal waterUnitPrice) {
        this.waterUnitPrice = waterUnitPrice;
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

    public BigDecimal getTotalNetIncome() {
        return totalNetIncome;
    }

    public void setTotalNetIncome(BigDecimal totalNetIncome) {
        this.totalNetIncome = totalNetIncome;
    }

    public BigDecimal getTotalNetExpense() {
        return totalNetExpense;
    }

    public void setTotalNetExpense(BigDecimal totalNetExpense) {
        this.totalNetExpense = totalNetExpense;
    }

    public BigDecimal getQuantitiy() {
        return quantitiy;
    }

    public void setQuantitiy(BigDecimal quantitiy) {
        this.quantitiy = quantitiy;
    }

    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

}

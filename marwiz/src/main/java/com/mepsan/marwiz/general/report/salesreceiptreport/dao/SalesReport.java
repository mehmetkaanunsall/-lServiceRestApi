/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 10:40:22
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.UserData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesReport extends Sales {

    private Date beginDate;
    private Date endDate;
    private BigDecimal minSalesPrice;
    private BigDecimal maxSalesPrice;
    private BigDecimal subTotalMoney;
    private int subTotalCount;
    private int subTotal;
    private List<String> saleTypeList;
    private Date saleDate;
    private List<UserData> listOfCashier;
    private int cashRegisterReceipt;
    private BranchSetting branchSetting;

    private int cardOperationCount;
    private int discountCount;

    public SalesReport() {
        this.saleTypeList = new ArrayList<>();
        this.listOfCashier = new ArrayList<>();
        this.branchSetting = new BranchSetting();
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

    public List<String> getSaleTypeList() {
        return saleTypeList;
    }

    public void setSaleTypeList(List<String> saleTypeList) {
        this.saleTypeList = saleTypeList;
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

    public BigDecimal getSubTotalMoney() {
        return subTotalMoney;
    }

    public void setSubTotalMoney(BigDecimal subTotalMoney) {
        this.subTotalMoney = subTotalMoney;
    }

    public int getSubTotalCount() {
        return subTotalCount;
    }

    public void setSubTotalCount(int subTotalCount) {
        this.subTotalCount = subTotalCount;
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

    public List<UserData> getListOfCashier() {
        return listOfCashier;
    }

    public void setListOfCashier(List<UserData> listOfCashier) {
        this.listOfCashier = listOfCashier;
    }

    public int getCashRegisterReceipt() {
        return cashRegisterReceipt;
    }

    public void setCashRegisterReceipt(int cashRegisterReceipt) {
        this.cashRegisterReceipt = cashRegisterReceipt;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public int getCardOperationCount() {
        return cardOperationCount;
    }

    public void setCardOperationCount(int cardOperationCount) {
        this.cardOperationCount = cardOperationCount;
    }

    public int getDiscountCount() {
        return discountCount;
    }

    public void setDiscountCount(int discountCount) {
        this.discountCount = discountCount;
    }

    @Override
    public String toString() {
        return this.getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:02:29 PM
 */
package com.mepsan.marwiz.general.report.zreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZReport {

    private int id;
    private Date beginDate;
    private Date endDate;
    private String category;
    private BigDecimal saleAmount;
    private BigDecimal salePrice;
    private String description;
    private int categoryId;
    private BigDecimal taxRate;
    private BigDecimal returnAmount;
    private BigDecimal returnPrice;
    private Currency currency;
    private Type type;
    private int receiptCount;
    private int returnReceiptCount;
    private BigDecimal totalSalePrice;
    private BigDecimal totalSaleMoney;
    private BigDecimal totalReturnPrice;
    private BigDecimal totalMoneyIncludeReturn;
    private List<BranchSetting> selectedBranchList;
    private Branch branch;
    private UserData userData;

    private BigDecimal subTotalSalePrice;
    private BigDecimal subTotalReturnPrice;

    public ZReport() {
        this.currency = new Currency();
        this.type = new Type();
        selectedBranchList = new ArrayList<>();
        this.branch = new Branch();
        this.userData = new UserData();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(BigDecimal saleAmount) {
        this.saleAmount = saleAmount;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    public BigDecimal getReturnPrice() {
        return returnPrice;
    }

    public void setReturnPrice(BigDecimal returnPrice) {
        this.returnPrice = returnPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public void setReceiptCount(int receiptCount) {
        this.receiptCount = receiptCount;
    }

    public int getReturnReceiptCount() {
        return returnReceiptCount;
    }

    public void setReturnReceiptCount(int returnReceiptCount) {
        this.returnReceiptCount = returnReceiptCount;
    }

    public BigDecimal getTotalSalePrice() {
        return totalSalePrice;
    }

    public void setTotalSalePrice(BigDecimal totalSalePrice) {
        this.totalSalePrice = totalSalePrice;
    }

    public BigDecimal getTotalSaleMoney() {
        return totalSaleMoney;
    }

    public void setTotalSaleMoney(BigDecimal totalSaleMoney) {
        this.totalSaleMoney = totalSaleMoney;
    }

    public BigDecimal getTotalReturnPrice() {
        return totalReturnPrice;
    }

    public void setTotalReturnPrice(BigDecimal totalReturnPrice) {
        this.totalReturnPrice = totalReturnPrice;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public BigDecimal getSubTotalSalePrice() {
        return subTotalSalePrice;
    }

    public void setSubTotalSalePrice(BigDecimal subTotalSalePrice) {
        this.subTotalSalePrice = subTotalSalePrice;
    }

    public BigDecimal getSubTotalReturnPrice() {
        return subTotalReturnPrice;
    }

    public void setSubTotalReturnPrice(BigDecimal subTotalReturnPrice) {
        this.subTotalReturnPrice = subTotalReturnPrice;
    }

    public BigDecimal getTotalMoneyIncludeReturn() {
        return totalMoneyIncludeReturn;
    }

    public void setTotalMoneyIncludeReturn(BigDecimal totalMoneyIncludeReturn) {
        this.totalMoneyIncludeReturn = totalMoneyIncludeReturn;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

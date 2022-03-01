/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 03.01.2019 11:20:17
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceReport {

    private Date beginDate;
    private Date endDate;
    private List<Account> accountList;
    private List<Stock> stockList;
    private int type;//satış,satınalma hepsi
    private BigDecimal salePriceMin;
    private BigDecimal salePriceMax;
    private boolean isTaxIncluded;
    private List<BranchSetting> selectedBranchList;
    private List<Type> listOfInvoiceType;

    public InvoiceReport() {
        this.accountList = new ArrayList<>();
        this.stockList = new ArrayList<>();
        this.selectedBranchList = new ArrayList<>();
        this.listOfInvoiceType = new ArrayList<>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public BigDecimal getSalePriceMin() {
        return salePriceMin;
    }

    public void setSalePriceMin(BigDecimal salePriceMin) {
        this.salePriceMin = salePriceMin;
    }

    public BigDecimal getSalePriceMax() {
        return salePriceMax;
    }

    public void setSalePriceMax(BigDecimal salePriceMax) {
        this.salePriceMax = salePriceMax;
    }

    public boolean isIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<Type> getListOfInvoiceType() {
        return listOfInvoiceType;
    }

    public void setListOfInvoiceType(List<Type> listOfInvoiceType) {
        this.listOfInvoiceType = listOfInvoiceType;
    }

}

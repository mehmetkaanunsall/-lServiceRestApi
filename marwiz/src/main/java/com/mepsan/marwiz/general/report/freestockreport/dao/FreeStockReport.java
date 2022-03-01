/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2020 04:20:01
 */
package com.mepsan.marwiz.general.report.freestockreport.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FreeStockReport extends InvoiceItem {

    private Date beginDate;
    private Date endDate;
    private List<Stock> stockList;
    private List<Categorization> listOfCategorization;
    private String category;
    private List<Account> listOfAccount;
    private List<CentralSupplier> listOfCentralSupplier;
    private BranchSetting branchSetting;

    public FreeStockReport() {
        this.stockList = new ArrayList<>();
        this.listOfCategorization = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
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

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

}

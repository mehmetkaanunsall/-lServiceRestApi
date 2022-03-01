/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 12:27:20
 */
package com.mepsan.marwiz.finance.customeragreements.dao;

import com.mepsan.marwiz.general.model.finance.Credit;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerAgreements extends Credit {

    private Date creditDate;
    private Date beginDate;
    private Date endDate;
    private List<Account> listOfAccount;//cari listesini tutar
    private List<Branch> selectedBranchList;//seçilen şubeleri tutar
    int invoiceType;
    private boolean isCheck;//Credit alanındaki checkbox ı kontrol eder
    private boolean chcCredit;//Credit alanındaki checkbox ı kontrol eder
    String creditIds;
    private String plate;//araç plakası
    private String stockName;//ürün
    public Stock stock;
    private double liter;//litre
    private double unitPrice;//birim fiyatı
    private int creditType;//combox daki kredi tiplerini kontrol eder
    private String findAccount;
    private boolean checkAll;
    private int rowNumberId;
    private String selectedAccountCount;

    public int getCreditType() {
        return creditType;
    }

    public void setCreditType(int creditType) {
        this.creditType = creditType;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getLiter() {
        return liter;
    }

    public void setLiter(double liter) {
        this.liter = liter;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getCreditIds() {
        return creditIds;
    }

    public void setCreditIds(String creditIds) {
        this.creditIds = creditIds;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public CustomerAgreements() {

        listOfAccount = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        this.stock = new Stock();

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

    public List<Branch> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<Branch> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public Date getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(Date creditDate) {
        this.creditDate = creditDate;
    }

    public boolean isChcCredit() {
        return chcCredit;
    }

    public void setChcCredit(boolean chcCredit) {
        this.chcCredit = chcCredit;
    }

    public String getFindAccount() {
        return findAccount;
    }

    public void setFindAccount(String findAccount) {
        this.findAccount = findAccount;
    }

    public boolean isCheckAll() {
        return checkAll;
    }

    public void setCheckAll(boolean checkAll) {
        this.checkAll = checkAll;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getRowNumberId() {
        return rowNumberId;
    }

    public void setRowNumberId(int rowNumberId) {
        this.rowNumberId = rowNumberId;
    }

    public String getSelectedAccountCount() {
        return selectedAccountCount;
    }

    public void setSelectedAccountCount(String selectedAccountCount) {
        this.selectedAccountCount = selectedAccountCount;
    }

}

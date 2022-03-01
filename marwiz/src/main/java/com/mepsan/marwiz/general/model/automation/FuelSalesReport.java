/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.automation;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ebubekir.buker
 */
public class FuelSalesReport extends FuelShiftSales {

    private Date beginDate;
    private Date endDate;
    private BigDecimal minSalesPrice;
    private BigDecimal maxSalesPrice;
    private List<CentralSupplier> listOfCentralSupplier;
    private BigDecimal subTotalMoney;

    private int subTotalCount;
    private int subTotal;
    private List<Account> listOfPumper;
    private List<Account> listOfAccount;
    private Branch branch;
    private CentralSupplier centralSupplier;
    
    private String centralProductCode;
    private String stockBarcode;

    private List<Branch> selectedBranchList;
    private List<FuelSaleType> selectedFuelSaleTypeList;
    

    public Stock stock;
    
    private Currency currency;
    
    private String shiftNo;
    
    public FuelSalesReport() {
        this.listOfCentralSupplier = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.listOfPumper = new ArrayList<>();
        
        this.centralSupplier = new CentralSupplier();
        this.branch=new Branch();
        this.selectedBranchList = new ArrayList<>();
        this.selectedFuelSaleTypeList = new ArrayList<>();
       
        this.currency = new Currency();
        this.stock=new Stock();
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

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
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

    public List<Account> getListOfPumper() {
        return listOfPumper;
    }

    public void setListOfPumper(List<Account> listOfPumper) {
        this.listOfPumper = listOfPumper;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

  
    public CentralSupplier getCentralSupplier() {
        return centralSupplier;
    }

    public void setCentralSupplier(CentralSupplier centralSupplier) {
        this.centralSupplier = centralSupplier;
    }

    public String getCentralProductCode() {
        return centralProductCode;
    }

    public void setCentralProductCode(String centralProductCode) {
        this.centralProductCode = centralProductCode;
    }
    
    

    public String getStockBarcode() {
        return stockBarcode;
    }

    public void setStockBarcode(String stockBarcode) {
        this.stockBarcode = stockBarcode;
    }

    public List<Branch> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<Branch> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }
  
    
   
    public List<FuelSaleType> getSelectedFuelSaleTypeList() {
        return selectedFuelSaleTypeList;
    }

    public void setSelectedFuelSaleTypeList(List<FuelSaleType> selectedFuelSaleTypeList) {
        this.selectedFuelSaleTypeList = selectedFuelSaleTypeList;
    }

    
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
    

}

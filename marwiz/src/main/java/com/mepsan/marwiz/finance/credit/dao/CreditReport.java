/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import com.mepsan.marwiz.general.model.finance.Credit;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public class CreditReport extends Credit {

    private boolean isPaid;
    private Date processDate;
    private BigDecimal overallmoney;
    private BigDecimal overallremainingmoney;
    private BigDecimal overallPaymentMoney;
    private BigDecimal overallPaymentRemaining;
    private Date beginDate;//Tabloda yok xhtml tarafında kullanıldığı için eklendi.
    private Date endDate;//Tabloda yok xhtml tarafında kullanıldığı için eklendi.
    private List<Account> accountList;//Tabloda yok
    private List<Branch> branchList;//Tabloda yok
    private BigDecimal paidMoney ; 
    
    private BigDecimal totalShiftCredit; // Tabloda yok .

    public CreditReport() {
        accountList = new ArrayList<>();
        branchList = new ArrayList<>();
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
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

    public boolean isIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public BigDecimal getOverallmoney() {
        return overallmoney;
    }

    public void setOverallmoney(BigDecimal overallmoney) {
        this.overallmoney = overallmoney;
    }

    public BigDecimal getOverallremainingmoney() {
        return overallremainingmoney;
    }

    public void setOverallremainingmoney(BigDecimal overallremainingmoney) {
        this.overallremainingmoney = overallremainingmoney;
    }

    public BigDecimal getOverallPaymentMoney() {
        return overallPaymentMoney;
    }

    public void setOverallPaymentMoney(BigDecimal overallPaymentMoney) {
        this.overallPaymentMoney = overallPaymentMoney;
    }

    public BigDecimal getOverallPaymentRemaining() {
        return overallPaymentRemaining;
    }

    public void setOverallPaymentRemaining(BigDecimal overallPaymentRemaining) {
        this.overallPaymentRemaining = overallPaymentRemaining;
    }

    public BigDecimal getTotalShiftCredit() {
        return totalShiftCredit;
    }

    public void setTotalShiftCredit(BigDecimal totalShiftCredit) {
        this.totalShiftCredit = totalShiftCredit;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }
    
    public BigDecimal getPaidMoney() {
        return paidMoney;
    }

    public void setPaidMoney(BigDecimal paidMoney) {
        this.paidMoney = paidMoney;
    }

    
    
}

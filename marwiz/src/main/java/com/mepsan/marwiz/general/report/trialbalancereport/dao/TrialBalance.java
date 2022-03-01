package com.mepsan.marwiz.general.report.trialbalancereport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author samet.dag
 */
public class TrialBalance implements Cloneable {

    public String accountName;
    public BigDecimal expense;
    public BigDecimal income;
    public String name;
    public Date beginDate;
    public Date endDate;
    private String incomeText;
    private String expenseText;
    private List<BranchSetting> selectedBranchList;
    private String branchName;

    public TrialBalance() {
        this.expense = BigDecimal.ZERO;
        this.income = BigDecimal.ZERO;
        this.selectedBranchList = new ArrayList<>();
    }

    public TrialBalance(String name, String branchName, BigDecimal income, BigDecimal expense) {
        this.name = name;
        this.income = income;
        this.expense = expense;
        this.branchName = branchName;

    }
    
    public TrialBalance(String name, String accName, String branchName, BigDecimal income, BigDecimal expense) {     
        this.name = name;
        this.accountName=accName;
        this.income = income;
        this.expense = expense;
        this.branchName = branchName;

    }
    

    public String getIncomeText() {
        return incomeText;
    }

    public void setIncomeText(String incomeText) {
        this.incomeText = incomeText;
    }

    public String getExpenseText() {
        return expenseText;
    }

    public void setExpenseText(String expenseText) {
        this.expenseText = expenseText;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    //override clone method of Object class
    @Override
    public TrialBalance clone() throws CloneNotSupportedException {
        return (TrialBalance) super.clone();
    }

}

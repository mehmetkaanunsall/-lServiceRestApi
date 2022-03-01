/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   12.03.2018 11:49:37
 */
package com.mepsan.marwiz.general.report.accountextract.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountExtract extends Account {

    private int account_branch_con_id;
    private BigDecimal inComing;
    private BigDecimal outComing;
    private Date beginDate;
    private Date endDate;
    private Branch branch;

    private List<Account> accountList;
    private List<Categorization> categorizationList;
    private List<Branch> branchList;

    private Date termDate;
    private int termDateOpType;

    public AccountExtract() {
        this.accountList = new ArrayList<>();
        this.categorizationList = new ArrayList<>();
        this.branchList = new ArrayList<>();
        this.branch = new Branch();
    }

    public BigDecimal getInComing() {
        return inComing;
    }

    public void setInComing(BigDecimal inComing) {
        this.inComing = inComing;
    }

    public BigDecimal getOutComing() {
        return outComing;
    }

    public void setOutComing(BigDecimal outComing) {
        this.outComing = outComing;
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

    public List<Categorization> getCategorizationList() {
        return categorizationList;
    }

    public void setCategorizationList(List<Categorization> categorizationList) {
        this.categorizationList = categorizationList;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getTermDate() {
        return termDate;
    }

    public void setTermDate(Date termDate) {
        this.termDate = termDate;
    }

    public int getTermDateOpType() {
        return termDateOpType;
    }

    public void setTermDateOpType(int termDateOpType) {
        this.termDateOpType = termDateOpType;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public int getAccount_branch_con_id() {
        return account_branch_con_id;
    }

    public void setAccount_branch_con_id(int account_branch_con_id) {
        this.account_branch_con_id = account_branch_con_id;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.account_branch_con_id;
    }

}

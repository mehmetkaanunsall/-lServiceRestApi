package com.mepsan.marwiz.general.model.general;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Samet Dağ
 */
public class EmployeeInfo {

    private int id;
    private Account account;
    private String integrationcode;
    private BigDecimal exactsalary;
    private int agi;
    private Date startdate;
    private Date enddate;
    public AccountMovement accountMovement;//Tabloda yok personelin borçlarını getirmek için eklendi.

    public EmployeeInfo() {
        account = new Account();
        accountMovement = new AccountMovement();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AccountMovement getAccountMovement() {
        return accountMovement;
    }

    public void setAccountMovement(AccountMovement accountMovement) {
        this.accountMovement = accountMovement;
    }

    public String getIntegrationcode() {
        return integrationcode;
    }

    public void setIntegrationcode(String integrationcode) {
        this.integrationcode = integrationcode;
    }

    public BigDecimal getExactsalary() {
        return exactsalary;
    }

    public void setExactsalary(BigDecimal exactsalary) {
        this.exactsalary = exactsalary;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

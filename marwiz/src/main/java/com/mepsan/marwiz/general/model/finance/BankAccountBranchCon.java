/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:19:21
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class BankAccountBranchCon extends WotLogging {

    private int id;
    private Branch branch;
    private BigDecimal balance;
    private BigDecimal commissionRate;
    private IncomeExpense commissionIncomeExpense;
    private BankAccount commissionBankAccount;

    public BankAccountBranchCon() {
        this.branch = new Branch();
        this.commissionIncomeExpense = new IncomeExpense();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public IncomeExpense getCommissionIncomeExpense() {
        return commissionIncomeExpense;
    }

    public void setCommissionIncomeExpense(IncomeExpense commissionIncomeExpense) {
        this.commissionIncomeExpense = commissionIncomeExpense;
    }

    public BankAccount getCommissionBankAccount() {
        return commissionBankAccount;
    }

    public void setCommissionBankAccount(BankAccount commissionBankAccount) {
        this.commissionBankAccount = commissionBankAccount;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

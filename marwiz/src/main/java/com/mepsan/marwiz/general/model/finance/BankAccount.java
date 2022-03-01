/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:19:31
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Size;

public class BankAccount extends WotLogging {

    private int id;
    private BankBranch bankBranch;
    @Size(max = 100)
    private String name;
    @Size(max = 60)
    private String accountNumber;
    @Size(max = 100)
    private String ibanNumber;
    private Currency currency;
    private Type type;
    private Status status;
    private BigDecimal balance;
    private BankAccountBranchCon bankAccountBranchCon;

    private BigDecimal creditCardLimit;
    private Date cutOffDate;
    private Date paymentDueDate;

    public BankAccount() {
        this.bankBranch = new BankBranch();
        this.currency = new Currency();
        this.type = new Type();
        this.status = new Status();
        this.bankAccountBranchCon = new BankAccountBranchCon();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    public void setIbanNumber(String ibanNumber) {
        this.ibanNumber = ibanNumber;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BankAccountBranchCon getBankAccountBranchCon() {
        return bankAccountBranchCon;
    }

    public void setBankAccountBranchCon(BankAccountBranchCon bankAccountBranchCon) {
        this.bankAccountBranchCon = bankAccountBranchCon;
    }

    public BigDecimal getCreditCardLimit() {
        return creditCardLimit;
    }

    public void setCreditCardLimit(BigDecimal creditCardLimit) {
        this.creditCardLimit = creditCardLimit;
    }

    public Date getCutOffDate() {
        return cutOffDate;
    }

    public void setCutOffDate(Date cutOffDate) {
        this.cutOffDate = cutOffDate;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 10:40:10
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Size;

public class FinancingDocument extends WotLogging {

    private int id;
    private Type financingType;
    @Size(max = 60)
    private String actualNumber;
    private String documentNumber;
    private Date documentDate;
    private BigDecimal exchangeRate;
    private String description;
    private Currency currency;
    private BigDecimal price;

    private int inMovementId;
    private int outMovementId;
    private Account account;
    private IncomeExpense incomeExpense;
    private BigDecimal total;
    private Branch branch;
    private Branch transferBranch;
    private int bankAccountCommissionId;

    public FinancingDocument(int id) {
        this.id = id;
    }

    public FinancingDocument() {
        this.currency = new Currency();
        this.financingType = new Type();
        this.account = new Account();
        this.incomeExpense = new IncomeExpense();
        this.branch = new Branch();
        this.transferBranch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getFinancingType() {
        return financingType;
    }

    public void setFinancingType(Type financingType) {
        this.financingType = financingType;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInMovementId() {
        return inMovementId;
    }

    public void setInMovementId(int inMovementId) {
        this.inMovementId = inMovementId;
    }

    public int getOutMovementId() {
        return outMovementId;
    }

    public void setOutMovementId(int outMovementId) {
        this.outMovementId = outMovementId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(String actualNumber) {
        this.actualNumber = actualNumber;
    }

    public IncomeExpense getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public BigDecimal getTotal() {
        if (price != null && exchangeRate != null) {
            return price.multiply(exchangeRate);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Branch getTransferBranch() {
        return transferBranch;
    }

    public void setTransferBranch(Branch transferBranch) {
        this.transferBranch = transferBranch;
    }

    public int getBankAccountCommissionId() {
        return bankAccountCommissionId;
    }

    public void setBankAccountCommissionId(int bankAccountCommissionId) {
        this.bankAccountCommissionId = bankAccountCommissionId;
    }
    

    @Override
    public String toString() {
        return this.documentNumber;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

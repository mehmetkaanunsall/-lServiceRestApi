/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:23:17 PM
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Size;

public class TransferCustomerToCustomer {

    private int id;
    private int financeTypeId;
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
    private Account inAccount;
    private Account outAccount;
    private BigDecimal total;
    private Branch branch;

    public TransferCustomerToCustomer() {
        this.inAccount = new Account();
        this.outAccount = new Account();
        this.currency = new Currency();
        this.financingType = new Type();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFinanceTypeId() {
        return financeTypeId;
    }

    public void setFinanceTypeId(int financeTypeId) {
        this.financeTypeId = financeTypeId;
    }

    public String getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(String actualNumber) {
        this.actualNumber = actualNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public Account getInAccount() {
        return inAccount;
    }

    public void setInAccount(Account inAccount) {
        this.inAccount = inAccount;
    }

    public Account getOutAccount() {
        return outAccount;
    }

    public void setOutAccount(Account outAccount) {
        this.outAccount = outAccount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Type getFinancingType() {
        return financingType;
    }

    public void setFinancingType(Type financingType) {
        this.financingType = financingType;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}

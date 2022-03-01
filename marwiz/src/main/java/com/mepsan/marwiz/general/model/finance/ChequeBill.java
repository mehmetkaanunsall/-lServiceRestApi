/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBill extends WotLogging {

    private int id;
    private boolean isCheque;
    private boolean isCustomer;
    private String portfolioNumber;
    private Date expiryDate;
    private BankBranch bankBranch;
    private DocumentNumber documentNumber;
    private Country country;
    private City paymentCity;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal totalMoney;
    private Account account;
    private String accountNumber;
    private String ibanNumber;
    private Status status;
    private Date billCollocationDate;
    private String documentNo;
    private String documentSerial;
    private BigDecimal remainingMoney;
    private String accountGuarantor;
    private Account endorsedAccount;
    private Branch branch;
    private BankAccount collectingBankAccount;

    public ChequeBill() {
        this.bankBranch = new BankBranch();
        this.account = new Account();
        this.documentNumber = new DocumentNumber();
        this.country = new Country();
        this.paymentCity = new City();
        this.currency = new Currency();
        this.endorsedAccount = new Account();
        this.status = new Status();
        this.branch = new Branch();
        this.collectingBankAccount = new BankAccount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsCheque() {
        return isCheque;
    }

    public void setIsCheque(boolean isCheque) {
        this.isCheque = isCheque;
    }

    public boolean isIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(boolean isCustomer) {
        this.isCustomer = isCustomer;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getAccountGuarantor() {
        return accountGuarantor;
    }

    public void setAccountGuarantor(String accountGuarantor) {
        this.accountGuarantor = accountGuarantor;
    }

    public Account getEndorsedAccount() {
        return endorsedAccount;
    }

    public void setEndorsedAccount(Account endorsedAccount) {
        this.endorsedAccount = endorsedAccount;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(DocumentNumber documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getPaymentCity() {
        return paymentCity;
    }

    public void setPaymentCity(City paymentCity) {
        this.paymentCity = paymentCity;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getBillCollocationDate() {
        return billCollocationDate;
    }

    public void setBillCollocationDate(Date billCollocationDate) {
        this.billCollocationDate = billCollocationDate;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getDocumentSerial() {
        return documentSerial;
    }

    public void setDocumentSerial(String documentSerial) {
        this.documentSerial = documentSerial;
    }

    public BigDecimal getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(BigDecimal remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public BankAccount getCollectingBankAccount() {
        return collectingBankAccount;
    }

    public void setCollectingBankAccount(BankAccount collectingBankAccount) {
        this.collectingBankAccount = collectingBankAccount;
    }

    @Override
    public String toString() {
        return this.portfolioNumber;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

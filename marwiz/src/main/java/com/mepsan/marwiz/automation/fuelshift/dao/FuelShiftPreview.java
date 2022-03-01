/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.02.2019 03:33:46
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;

public class FuelShiftPreview {

    private Stock stock;
    private BigDecimal previousAmount;
    private BigDecimal remainingAmount;
    private BigDecimal amount;
    private BigDecimal totalMoney;

    private Safe safe;
    private BigDecimal price;
    private BigDecimal exchangeRate;

    private BankAccount bankAccount;

    private Account account;
    private int saleCount;

    private BigDecimal incomingAmount;
    private BigDecimal outcomingAmount;
    private String documentNumber;
    private String description;
    private Type type;

    private BigDecimal creditAmout;
    private BigDecimal automationSaleAmount;
    private BigDecimal testAmount;
    private BigDecimal cashAmount;
    private BigDecimal incomeAmount;
    private BigDecimal expenseAmount;
    private BigDecimal employeeDebt;
    private BigDecimal givenEmployee;
    private BigDecimal creditCardAmount;
    private BigDecimal entrySubTotal;
    private BigDecimal exitSubTotal;
    private BigDecimal accountCollection;
    private BigDecimal accountPayment;

    public FuelShiftPreview() {
        this.stock = new Stock();
        this.safe = new Safe();
        this.bankAccount = new BankAccount();
        this.account = new Account();
        this.type = new Type();
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(BigDecimal previousAmount) {
        this.previousAmount = previousAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getIncomingAmount() {
        return incomingAmount;
    }

    public void setIncomingAmount(BigDecimal incomingAmount) {
        this.incomingAmount = incomingAmount;
    }

    public BigDecimal getOutcomingAmount() {
        return outcomingAmount;
    }

    public void setOutcomingAmount(BigDecimal outcomingAmount) {
        this.outcomingAmount = outcomingAmount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getCreditAmout() {
        return creditAmout;
    }

    public void setCreditAmout(BigDecimal creditAmout) {
        this.creditAmout = creditAmout;
    }

    public BigDecimal getAutomationSaleAmount() {
        return automationSaleAmount;
    }

    public void setAutomationSaleAmount(BigDecimal automationSaleAmount) {
        this.automationSaleAmount = automationSaleAmount;
    }

    public BigDecimal getTestAmount() {
        return testAmount;
    }

    public void setTestAmount(BigDecimal testAmount) {
        this.testAmount = testAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public BigDecimal getEmployeeDebt() {
        return employeeDebt;
    }

    public void setEmployeeDebt(BigDecimal employeeDebt) {
        this.employeeDebt = employeeDebt;
    }

    public BigDecimal getGivenEmployee() {
        return givenEmployee;
    }

    public void setGivenEmployee(BigDecimal givenEmployee) {
        this.givenEmployee = givenEmployee;
    }

    public BigDecimal getCreditCardAmount() {
        return creditCardAmount;
    }

    public void setCreditCardAmount(BigDecimal creditCardAmount) {
        this.creditCardAmount = creditCardAmount;
    }

    public BigDecimal getEntrySubTotal() {
        return entrySubTotal;
    }

    public void setEntrySubTotal(BigDecimal entrySubTotal) {
        this.entrySubTotal = entrySubTotal;
    }

    public BigDecimal getExitSubTotal() {
        return exitSubTotal;
    }

    public void setExitSubTotal(BigDecimal exitSubTotal) {
        this.exitSubTotal = exitSubTotal;
    }

    public BigDecimal getAccountCollection() {
        return accountCollection;
    }

    public void setAccountCollection(BigDecimal accountCollection) {
        this.accountCollection = accountCollection;
    }

    public BigDecimal getAccountPayment() {
        return accountPayment;
    }

    public void setAccountPayment(BigDecimal accountPayment) {
        this.accountPayment = accountPayment;
    }

}

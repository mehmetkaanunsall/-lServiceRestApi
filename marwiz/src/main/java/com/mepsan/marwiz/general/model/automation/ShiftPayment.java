package com.mepsan.marwiz.general.model.automation;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.Credit;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Samet Dağ 15.10.2018
 */
public class ShiftPayment {

    private int id;
    private Date processDate;
    private int currency_id;
    private FinancingDocument financingDocument;
    private Account account;
    private FuelShift shift;
    private FuelSaleType fuelSaleType;
    private BigDecimal price;
    private BankAccount bankAccount;
    private Safe safe;
    private Credit credit;
    private boolean isAutomation;

    private boolean isDebiting;
    private boolean isReverseMove;
    private Account attendantAccount;

    public ShiftPayment() {
        this.financingDocument = new FinancingDocument();
        this.account = new Account();
        this.shift = new FuelShift();
        this.fuelSaleType = new FuelSaleType();
        this.bankAccount = new BankAccount();
        this.safe = new Safe();
        this.credit = new Credit();
        this.attendantAccount = new Account();
    }

    public boolean isIsDebiting() {
        return isDebiting;
    }

    public void setIsDebiting(boolean isDebiting) {
        this.isDebiting = isDebiting;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public FuelShift getShift() {
        return shift;
    }

    public void setShift(FuelShift shift) {
        this.shift = shift;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public FuelSaleType getFuelSaleType() {
        return fuelSaleType;
    }

    public void setFuelSaleType(FuelSaleType fuelSaleType) {
        this.fuelSaleType = fuelSaleType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public int getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(int currency_id) {
        this.currency_id = currency_id;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public boolean isIsAutomation() {
        return isAutomation;
    }

    public void setIsAutomation(boolean isAutomation) {
        this.isAutomation = isAutomation;
    }

    public boolean isIsReverseMove() {
        return isReverseMove;
    }

    public void setIsReverseMove(boolean isReverseMove) {
        this.isReverseMove = isReverseMove;
    }

    public Account getAttendantAccount() {
        return attendantAccount;
    }

    public void setAttendantAccount(Account attendantAccount) {
        this.attendantAccount = attendantAccount;
    }

}

/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:13:02
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class InvoicePayment extends WotLogging {

    private int id;
    private Invoice invoice;
    private FinancingDocument financingDocument;
    public ChequeBill chequeBill;
    private Type type;
    private Credit credit;
    private BigDecimal price;
    private BigDecimal exchangeRate;
    private Currency currency;
    private boolean isDirection;
    private Date processDate;
    private Safe safe;
    private BankAccount bankAccount;

    public InvoicePayment() {
        this.invoice = new Invoice();
        this.financingDocument = new FinancingDocument();
        this.credit = new Credit();
        this.currency = new Currency();
        this.type = new Type();
        this.chequeBill = new ChequeBill();
        this.safe = new Safe();
        this.bankAccount = new BankAccount();

    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public ChequeBill getChequeBill() {
        return chequeBill;
    }

    public void setChequeBill(ChequeBill chequeBill) {
        this.chequeBill = chequeBill;
    }

    @Override
    public String toString() {
        return this.getInvoice().getDocumentNumber();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

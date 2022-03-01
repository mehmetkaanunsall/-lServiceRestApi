/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 05:45:19
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Receipt;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class AccountMovement extends WotLogging {

    private int id;
    private Account account;
    private FinancingDocument financingDocument;
    private boolean isDirection;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal price;
    private Date movementDate;
    private Invoice invoice;
    private ChequeBill chequeBill;
    private Receipt receipt;

    private BigDecimal transferringbalance;//gridde devreden bakıyeyı tutmak ıcın
    private BigDecimal balance;//gridde carinin hareket anındaki bakiyesini göstermek için 
    private int shiftId;//Hareket Vardiyadan Oluştu İse Vardiya Idsini Almak İçin Kullanılır.
    private int stockTakingId;//Hareket Sayımdan Oluştu İse Sayım Idsini Almak İçin Kullanılır.

    private BigDecimal totalOutcoming;
    private BigDecimal totalIncoming;
    private Branch branch;

    public AccountMovement(int id) {
        this.id = id;
    }

    public AccountMovement() {
        this.account = new Account();
        this.financingDocument = new FinancingDocument();
        this.currency = new Currency();
        this.invoice = new Invoice();
        this.chequeBill = new ChequeBill();
        this.receipt = new Receipt();
        this.branch= new Branch();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Date movementDate) {
        this.movementDate = movementDate;
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

    public BigDecimal getTransferringbalance() {
        return transferringbalance;
    }

    public void setTransferringbalance(BigDecimal transferringbalance) {
        this.transferringbalance = transferringbalance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalOutcoming() {
        return totalOutcoming;
    }

    public void setTotalOutcoming(BigDecimal totalOutcoming) {
        this.totalOutcoming = totalOutcoming;
    }

    public BigDecimal getTotalIncoming() {
        return totalIncoming;
    }

    public void setTotalIncoming(BigDecimal totalIncoming) {
        this.totalIncoming = totalIncoming;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ChequeBill getChequeBill() {
        return chequeBill;
    }

    public void setChequeBill(ChequeBill chequeBill) {
        this.chequeBill = chequeBill;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public int getStockTakingId() {
        return stockTakingId;
    }

    public void setStockTakingId(int stockTakingId) {
        this.stockTakingId = stockTakingId;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    

    @Override
    public String toString() {
        return this.financingDocument.getDocumentNumber();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

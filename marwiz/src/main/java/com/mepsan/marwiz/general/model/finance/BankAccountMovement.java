/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2017 11:35:41
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BankAccountMovement extends WotLogging {

    private int id;
    private BankAccount bankAccount;
    private FinancingDocument financingDocument;
    private boolean isDirection;
    private BigDecimal price;
    private Date movementDate;

    private BigDecimal transferringbalance;//gridde devreden bakıyeyı tutmak ıcın
    private BigDecimal balance;//gridde işlem anındaki bakiyeyi göstermek ıcın

    private BigDecimal totalOutcoming;
    private BigDecimal totalIncoming;

    private List<Branch> branchList;
    private Branch branch;

    public BankAccountMovement(int id) {
        this.id = id;
    }

    public BankAccountMovement() {
        this.bankAccount = new BankAccount();
        this.financingDocument = new FinancingDocument();
        this.branchList = new ArrayList<>();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
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

    public Date getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Date movementDate) {
        this.movementDate = movementDate;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return this.bankAccount.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

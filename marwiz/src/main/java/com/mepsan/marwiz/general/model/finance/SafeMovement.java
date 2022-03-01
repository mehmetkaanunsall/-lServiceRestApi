/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 05:22:49
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SafeMovement extends WotLogging {

    private int id;
    private Safe safe;
    private boolean isDirection;
    private BigDecimal price;
    private Date movementDate;
    private BigDecimal transferringbalance;//gridde devreden bakıyeyı tutmak ıcın
    private BigDecimal balance;//griddte işlem anındaki bakiyeyi göstermek ıcın
    private FinancingDocument financingDocument;

    private BigDecimal totalOutcoming;
    private BigDecimal totalIncoming;

    private Date beginDate;
    private Date endDate;
    private Branch branch;
    private List<Branch> listOfBranch;
    private List<Safe> listOfSafe;


    public SafeMovement() {
        this.safe = new Safe();
        this.financingDocument = new FinancingDocument();
        this.branch = new Branch();
        this.listOfBranch = new ArrayList<>();
        this.listOfSafe = new ArrayList<>();
    }

    public SafeMovement(Safe safe, FinancingDocument financingDocument) {
        this.safe = safe;
        this.financingDocument = financingDocument;
        this.branch = new Branch();
    }

    public SafeMovement(Safe safe) {
        this.safe = safe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
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

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
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

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<Safe> getListOfSafe() {
        return listOfSafe;
    }

    public void setListOfSafe(List<Safe> listOfSafe) {
        this.listOfSafe = listOfSafe;
    }

   

    @Override
    public String toString() {
        return this.safe.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

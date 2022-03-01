/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:45:07
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import java.math.BigDecimal;
import java.util.Date;

public class StockMovement extends WarehouseMovement {

    private Date moveDate;
    private Double remainingAmount;
    ///Stok Hareketleri Tabında İşlemin Fiş, İade, Fatura, Sayımdan Geldiğini kontrol Eder.
    private int processType;
    private Invoice invoice;
    private StockTaking stockTaking;
    private BigDecimal unitPrice;
    private Branch branch;
    private BigDecimal totalOutcoming;
    private BigDecimal totalIncoming;
    private BigDecimal transferAmount;

    public StockMovement() {
        this.invoice = new Invoice();
        this.stockTaking = new StockTaking();
        this.branch = new Branch();
    }

    public Date getMoveDate() {
        return moveDate;
    }

    public void setMoveDate(Date moveDate) {
        this.moveDate = moveDate;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
    
    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

}

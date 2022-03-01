/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.05.2020 10:49:59
 */
package com.mepsan.marwiz.general.model.inventory;

import java.util.Date;

public class WarehouseTransfer {

    private int id;
    private String receiptNumber;
    private WarehouseReceipt warehouseReceipt;
    private WarehouseReceipt transferWarehouseReceipt;
    private Date processDate;

    public WarehouseTransfer() {
        this.warehouseReceipt = new WarehouseReceipt();
        this.transferWarehouseReceipt = new WarehouseReceipt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WarehouseReceipt getWarehouseReceipt() {
        return warehouseReceipt;
    }

    public void setWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        this.warehouseReceipt = warehouseReceipt;
    }

    public WarehouseReceipt getTransferWarehouseReceipt() {
        return transferWarehouseReceipt;
    }

    public void setTransferWarehouseReceipt(WarehouseReceipt transferWarehouseReceipt) {
        this.transferWarehouseReceipt = transferWarehouseReceipt;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    @Override
    public String toString() {
        return this.receiptNumber;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 18:28:40
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class NozzleMovement extends WotLogging {

    private int id;
    private Nozzle nozzle;
    private FuelShift fuelShift;
    private Stock stock;
    private Date processDate;
    private String receiptNo;
    private BigDecimal firstIndex;
    private BigDecimal lastIndex;
    private BigDecimal differentIndex;

    public NozzleMovement() {
        this.nozzle = new Nozzle();
        this.stock = new Stock();
        this.fuelShift = new FuelShift();
        this.setUserCreated(new UserData());
    }

    public Nozzle getNozzle() {
        return nozzle;
    }

    public void setNozzle(Nozzle nozzle) {
        this.nozzle = nozzle;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public BigDecimal getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(BigDecimal firstIndex) {
        this.firstIndex = firstIndex;
    }

    public BigDecimal getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(BigDecimal lastIndex) {
        this.lastIndex = lastIndex;
    }

    public BigDecimal getDifferentIndex() {
        return differentIndex;
    }

    public void setDifferentIndex(BigDecimal differentIndex) {
        this.differentIndex = differentIndex;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FuelShift getFuelShift() {
        return fuelShift;
    }

    public void setFuelShift(FuelShift fuelShift) {
        this.fuelShift = fuelShift;
    }

    @Override
    public String toString() {
        return this.getReceiptNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

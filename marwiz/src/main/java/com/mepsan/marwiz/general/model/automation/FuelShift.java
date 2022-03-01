/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:28:16
 */
package com.mepsan.marwiz.general.model.automation;

import java.math.BigDecimal;
import java.util.Date;

public class FuelShift {

    private int id;
    private String shiftNo;
    private Date beginDate;
    private Date endDate;
    private boolean isConfirm;

    private BigDecimal totalMoney;
    private BigDecimal shiftPaymentTotal;
    private String shiftAttendant;
    private String incorrectRecord;
    private boolean isDeleted;
    private Date deletedTime;

    private BigDecimal totalSalesAmount;
    private BigDecimal creditCardPaymentPrice;
    private BigDecimal creditPaymentPrice;
    private BigDecimal ttsPaymentPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
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

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getShiftPaymentTotal() {
        return shiftPaymentTotal;
    }

    public void setShiftPaymentTotal(BigDecimal shiftPaymentTotal) {
        this.shiftPaymentTotal = shiftPaymentTotal;
    }

    public String getShiftAttendant() {
        return shiftAttendant;
    }

    public void setShiftAttendant(String shiftAttendant) {
        this.shiftAttendant = shiftAttendant;
    }

    public boolean isIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(boolean isConfirm) {
        this.isConfirm = isConfirm;
    }

    public String getIncorrectRecord() {
        return incorrectRecord;
    }

    public void setIncorrectRecord(String incorrectRecord) {
        this.incorrectRecord = incorrectRecord;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Date deletedTime) {
        this.deletedTime = deletedTime;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getCreditCardPaymentPrice() {
        return creditCardPaymentPrice;
    }

    public void setCreditCardPaymentPrice(BigDecimal creditCardPaymentPrice) {
        this.creditCardPaymentPrice = creditCardPaymentPrice;
    }

    public BigDecimal getCreditPaymentPrice() {
        return creditPaymentPrice;
    }

    public void setCreditPaymentPrice(BigDecimal creditPaymentPrice) {
        this.creditPaymentPrice = creditPaymentPrice;
    }

    public BigDecimal getTtsPaymentPrice() {
        return ttsPaymentPrice;
    }

    public void setTtsPaymentPrice(BigDecimal ttsPaymentPrice) {
        this.ttsPaymentPrice = ttsPaymentPrice;
    }

    @Override
    public String toString() {
        return this.getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

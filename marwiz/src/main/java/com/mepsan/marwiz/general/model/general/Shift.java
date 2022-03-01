/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 02:53:37
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class Shift extends WotLogging {

    private int id;
    private String name;
    private String shiftNo;
    private Date beginDate;
    private Date endDate;
    private Status status;

    private int saleCount;//database de yok vardiya raporları için eklendi
    private String totalSaleAmountString;
    private boolean is_ShiftPaymentCheck;
    private boolean is_Confirm;
    private boolean is_MovementSafe;
    private BigDecimal sumOfRemovedStock;
    private String actualPriceString;
    private boolean isAvailableSale;
    private String shiftPerson;

    private String description;

    public Shift() {
        this.status = new Status();
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public String getTotalSaleAmountString() {
        return totalSaleAmountString;
    }

    public void setTotalSaleAmountString(String totalSaleAmountString) {
        this.totalSaleAmountString = totalSaleAmountString;
    }

    public boolean isIs_ShiftPaymentCheck() {
        return is_ShiftPaymentCheck;
    }

    public void setIs_ShiftPaymentCheck(boolean is_ShiftPaymentCheck) {
        this.is_ShiftPaymentCheck = is_ShiftPaymentCheck;
    }

    public boolean isIs_Confirm() {
        return is_Confirm;
    }

    public void setIs_Confirm(boolean is_Confirm) {
        this.is_Confirm = is_Confirm;
    }

    public boolean isIs_MovementSafe() {
        return is_MovementSafe;
    }

    public void setIs_MovementSafe(boolean is_MovementSafe) {
        this.is_MovementSafe = is_MovementSafe;
    }

    public BigDecimal getSumOfRemovedStock() {
        return sumOfRemovedStock;
    }

    public void setSumOfRemovedStock(BigDecimal sumOfRemovedStock) {
        this.sumOfRemovedStock = sumOfRemovedStock;
    }

    public String getActualPriceString() {
        return actualPriceString;
    }

    public void setActualPriceString(String actualPriceString) {
        this.actualPriceString = actualPriceString;
    }

    public boolean isIsAvailableSale() {
        return isAvailableSale;
    }

    public void setIsAvailableSale(boolean isAvailableSale) {
        this.isAvailableSale = isAvailableSale;
    }

    public String getShiftPerson() {
        return shiftPerson;
    }

    public void setShiftPerson(String shiftPerson) {
        this.shiftPerson = shiftPerson;
    }

    @Override
    public String toString() {
        return this.getShiftNo();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

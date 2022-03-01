/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 06.02.2019 13:37:29
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import java.math.BigDecimal;
import java.util.Date;

public class TankMovement extends WarehouseMovement {

    private Invoice invoice;
    private FuelShift fuelShift;
    private Waybill waybill;

    private BigDecimal unitPriceWithoutTax;
    private BigDecimal unitPriceWithTax;
    private BigDecimal totalPrice;
    private BigDecimal totalMoney;
    private Date moveDate;
    private BigDecimal remainingAmount;
    private BigDecimal taxRate;
    private int type;//Hareketin Geldiği İşlem 1 Satış 2 : Fatura 0: Depo Fişi

    public TankMovement() {
        this.invoice = new Invoice();
        this.fuelShift = new FuelShift();
        this.waybill = new Waybill();
        this.setUserCreated(new UserData());
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public FuelShift getFuelShift() {
        return fuelShift;
    }

    public void setFuelShift(FuelShift fuelShift) {
        this.fuelShift = fuelShift;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public BigDecimal getUnitPriceWithoutTax() {
        return unitPriceWithoutTax;
    }

    public void setUnitPriceWithoutTax(BigDecimal unitPriceWithoutTax) {
        this.unitPriceWithoutTax = unitPriceWithoutTax;
    }

    public BigDecimal getUnitPriceWithTax() {
        return unitPriceWithTax;
    }

    public void setUnitPriceWithTax(BigDecimal unitPriceWithTax) {
        this.unitPriceWithTax = unitPriceWithTax;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Date getMoveDate() {
        return moveDate;
    }

    public void setMoveDate(Date moveDate) {
        this.moveDate = moveDate;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

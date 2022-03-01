/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author tugcan.koparan
 */
public class FuelShiftReport extends FuelShift {

    private Date reportBeginDate;
    private Date reportEndDate;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalSalesPrice;
    private BigDecimal incomePrice;
    private BigDecimal expensePrice;
    private BigDecimal cashPaymentPrice;
    private BigDecimal deficitSurplusPrice;
    private BigDecimal ttsPaymentPrice;
    private BigDecimal creditCardPaymentPrice;
    private Integer numberofVehicle;
    private BigDecimal creditPaymentPrice;

    private BigDecimal dkvPaymentPrice;
    private BigDecimal utaPaymentPrice;
    private BigDecimal presentPaymentPrice;
    private BigDecimal paroPaymentPrice;
    private BigDecimal fuelCardPaymentPrice;

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getTotalSalesPrice() {
        return totalSalesPrice;
    }

    public void setTotalSalesPrice(BigDecimal totalSalesPrice) {
        this.totalSalesPrice = totalSalesPrice;
    }

    public Date getReportBeginDate() {
        return reportBeginDate;
    }

    public void setReportBeginDate(Date reportBeginDate) {
        this.reportBeginDate = reportBeginDate;
    }

    public Date getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(Date reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public BigDecimal getIncomePrice() {
        return incomePrice;
    }

    public void setIncomePrice(BigDecimal incomePrice) {
        this.incomePrice = incomePrice;
    }

    public BigDecimal getExpensePrice() {
        return expensePrice;
    }

    public void setExpensePrice(BigDecimal expensePrice) {
        this.expensePrice = expensePrice;
    }

    public BigDecimal getDeficitSurplusPrice() {
        return deficitSurplusPrice;
    }

    public BigDecimal getCashPaymentPrice() {
        return cashPaymentPrice;
    }

    public void setCashPaymentPrice(BigDecimal cashPaymentPrice) {
        this.cashPaymentPrice = cashPaymentPrice;
    }

    public void setDeficitSurplusPrice(BigDecimal deficitSurplusPrice) {
        this.deficitSurplusPrice = deficitSurplusPrice;
    }

    public BigDecimal getTtsPaymentPrice() {
        return ttsPaymentPrice;
    }

    public void setTtsPaymentPrice(BigDecimal ttsPaymentPrice) {
        this.ttsPaymentPrice = ttsPaymentPrice;
    }

    public BigDecimal getCreditCardPaymentPrice() {
        return creditCardPaymentPrice;
    }

    public void setCreditCardPaymentPrice(BigDecimal creditCardPaymentPrice) {
        this.creditCardPaymentPrice = creditCardPaymentPrice;
    }

    public Integer getNumberofVehicle() {
        return numberofVehicle;
    }

    public void setNumberofVehicle(Integer numberofVehicle) {
        this.numberofVehicle = numberofVehicle;
    }

    public BigDecimal getCreditPaymentPrice() {
        return creditPaymentPrice;
    }

    public void setCreditPaymentPrice(BigDecimal creditPaymentPrice) {
        this.creditPaymentPrice = creditPaymentPrice;
    }

    public BigDecimal getDkvPaymentPrice() {
        return dkvPaymentPrice;
    }

    public void setDkvPaymentPrice(BigDecimal dkvPaymentPrice) {
        this.dkvPaymentPrice = dkvPaymentPrice;
    }

    public BigDecimal getUtaPaymentPrice() {
        return utaPaymentPrice;
    }

    public void setUtaPaymentPrice(BigDecimal utaPaymentPrice) {
        this.utaPaymentPrice = utaPaymentPrice;
    }

    public BigDecimal getPresentPaymentPrice() {
        return presentPaymentPrice;
    }

    public void setPresentPaymentPrice(BigDecimal presentPaymentPrice) {
        this.presentPaymentPrice = presentPaymentPrice;
    }

    public BigDecimal getParoPaymentPrice() {
        return paroPaymentPrice;
    }

    public void setParoPaymentPrice(BigDecimal paroPaymentPrice) {
        this.paroPaymentPrice = paroPaymentPrice;
    }

    public BigDecimal getFuelCardPaymentPrice() {
        return fuelCardPaymentPrice;
    }

    public void setFuelCardPaymentPrice(BigDecimal fuelCardPaymentPrice) {
        this.fuelCardPaymentPrice = fuelCardPaymentPrice;
    }

}

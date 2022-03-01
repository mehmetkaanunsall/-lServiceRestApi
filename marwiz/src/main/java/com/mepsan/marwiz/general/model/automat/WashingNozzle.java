/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:50:19 AM
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;

public class WashingNozzle {

    private int id;
    private WashingPlatform washingMachicnePlatform;
    private WashingTank washingMachicneTank;
    private String nozzleNo;
    private int operationTime;
    private BigDecimal operationAmount;
    private BigDecimal unitPrice;
    private Currency currency;
    private Unit unit; // tabloda yok ,stoğun birimi için kullanıldı
    private String stockName; // tabloda yok 
    private BigDecimal electricAmount;
    private BigDecimal waterAmount;

    public WashingNozzle() {
        this.washingMachicnePlatform = new WashingPlatform();
        this.washingMachicneTank = new WashingTank();
        this.currency = new Currency();
        this.unit = new Unit();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WashingPlatform getWashingMachicnePlatform() {
        return washingMachicnePlatform;
    }

    public void setWashingMachicnePlatform(WashingPlatform washingMachicnePlatform) {
        this.washingMachicnePlatform = washingMachicnePlatform;
    }

    public WashingTank getWashingMachicneTank() {
        return washingMachicneTank;
    }

    public void setWashingMachicneTank(WashingTank washingMachicneTank) {
        this.washingMachicneTank = washingMachicneTank;
    }

    public String getNozzleNo() {
        return nozzleNo;
    }

    public void setNozzleNo(String nozzleNo) {
        this.nozzleNo = nozzleNo;
    }

    public int getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(int operationTime) {
        this.operationTime = operationTime;
    }

    public BigDecimal getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(BigDecimal operationAmount) {
        this.operationAmount = operationAmount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getElectricAmount() {
        return electricAmount;
    }

    public void setElectricAmount(BigDecimal electricAmount) {
        this.electricAmount = electricAmount;
    }

    public BigDecimal getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(BigDecimal waterAmount) {
        this.waterAmount = waterAmount;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getNozzleNo();
    }

}

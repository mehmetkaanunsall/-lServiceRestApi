/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   06.03.2017 17:37:51
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

public class ChartItem {

    private int count;
    private Number number1;
    private Number number2;
    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String nameOther;
    private Currency currency1;
    private Currency oldCurrency;
    private Currency newCurrency;
    private BigDecimal bigDecimal1;
    private BigDecimal bigDecimal2;
    private BigDecimal bigDecimal3;
    private Unit unit;
    private int typeId; // tahsilat widgetinde gösterilen tipler için tutuldu.
    private int typeId2; // // tahsilat widgetinde gösterilen gecikmiş ödeme tipi için tutuldu
    private Date beginDate;
    private Date endDate;

    /*Yıkama makinesi karlılığı için açıldı */
    private BigDecimal quantity;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal waste;
    private BigDecimal electricQuantity;
    private BigDecimal electricOperationTime;
    private BigDecimal electricExpense;
    private int operationTime;
    private BigDecimal totalElectricAmount;
    private BigDecimal waterQuantity;
    private BigDecimal waterUnitPrice;
    private BigDecimal waterWorkingAmount;
    private int waterWorkingTime;
    private BigDecimal waterExpense;
    private BigDecimal waterWaste;
    private BigDecimal totalNetIncome;
    private BigDecimal totalNetExpense;
    private BigDecimal totalWinnings;
    private String unitName;
    private int stockId;
    private int month;

    private String name;

    public ChartItem() {
        this.currency1 = new Currency();
        this.unit = new Unit();
        this.oldCurrency = new Currency();
        this.newCurrency = new Currency();
    }

    public Number getNumber1() {
        return number1;
    }

    public void setNumber1(Number number1) {
        this.number1 = number1;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getWaste() {
        return waste;
    }

    public void setWaste(BigDecimal waste) {
        this.waste = waste;
    }

    public BigDecimal getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(BigDecimal electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public BigDecimal getElectricOperationTime() {
        return electricOperationTime;
    }

    public void setElectricOperationTime(BigDecimal electricOperationTime) {
        this.electricOperationTime = electricOperationTime;
    }

    public BigDecimal getElectricExpense() {
        return electricExpense;
    }

    public void setElectricExpense(BigDecimal electricExpense) {
        this.electricExpense = electricExpense;
    }

    public int getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(int operationTime) {
        this.operationTime = operationTime;
    }

    public BigDecimal getTotalElectricAmount() {
        return totalElectricAmount;
    }

    public void setTotalElectricAmount(BigDecimal totalElectricAmount) {
        this.totalElectricAmount = totalElectricAmount;
    }

    public BigDecimal getWaterQuantity() {
        return waterQuantity;
    }

    public void setWaterQuantity(BigDecimal waterQuantity) {
        this.waterQuantity = waterQuantity;
    }

    public BigDecimal getWaterUnitPrice() {
        return waterUnitPrice;
    }

    public void setWaterUnitPrice(BigDecimal waterUnitPrice) {
        this.waterUnitPrice = waterUnitPrice;
    }

    public BigDecimal getWaterWorkingAmount() {
        return waterWorkingAmount;
    }

    public void setWaterWorkingAmount(BigDecimal waterWorkingAmount) {
        this.waterWorkingAmount = waterWorkingAmount;
    }

    public int getWaterWorkingTime() {
        return waterWorkingTime;
    }

    public void setWaterWorkingTime(int waterWorkingTime) {
        this.waterWorkingTime = waterWorkingTime;
    }

    public BigDecimal getWaterExpense() {
        return waterExpense;
    }

    public void setWaterExpense(BigDecimal waterExpense) {
        this.waterExpense = waterExpense;
    }

    public BigDecimal getWaterWaste() {
        return waterWaste;
    }

    public void setWaterWaste(BigDecimal waterWaste) {
        this.waterWaste = waterWaste;
    }

    public BigDecimal getTotalNetIncome() {
        return totalNetIncome;
    }

    public void setTotalNetIncome(BigDecimal totalNetIncome) {
        this.totalNetIncome = totalNetIncome;
    }

    public BigDecimal getTotalNetExpense() {
        return totalNetExpense;
    }

    public void setTotalNetExpense(BigDecimal totalNetExpense) {
        this.totalNetExpense = totalNetExpense;
    }

    public BigDecimal getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(BigDecimal totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Number getNumber2() {
        return number2;
    }

    public void setNumber2(Number number2) {
        this.number2 = number2;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getNameOther() {
        return nameOther;
    }

    public void setNameOther(String nameOther) {
        this.nameOther = nameOther;
    }

    public Currency getCurrency1() {
        return currency1;
    }

    public void setCurrency1(Currency currency1) {
        this.currency1 = currency1;
    }

    public BigDecimal getBigDecimal1() {
        return bigDecimal1;
    }

    public void setBigDecimal1(BigDecimal bigDecimal1) {
        this.bigDecimal1 = bigDecimal1;
    }

    public BigDecimal getBigDecimal2() {
        return bigDecimal2;
    }

    public void setBigDecimal2(BigDecimal bigDecimal2) {
        this.bigDecimal2 = bigDecimal2;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId2() {
        return typeId2;
    }

    public void setTypeId2(int typeId2) {
        this.typeId2 = typeId2;
    }

    public BigDecimal getBigDecimal3() {
        return bigDecimal3;
    }

    public void setBigDecimal3(BigDecimal bigDecimal3) {
        this.bigDecimal3 = bigDecimal3;
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

    public Currency getOldCurrency() {
        return oldCurrency;
    }

    public void setOldCurrency(Currency oldCurrency) {
        this.oldCurrency = oldCurrency;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public Currency getNewCurrency() {
        return newCurrency;
    }

    public void setNewCurrency(Currency newCurrency) {
        this.newCurrency = newCurrency;
    }

}

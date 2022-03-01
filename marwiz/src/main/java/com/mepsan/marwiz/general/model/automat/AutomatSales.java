/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 12:03:39
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class AutomatSales extends WotLogging {

    private int id;
    private WashingMachicne washingMachine;
    private String macAddress;
    private WashingTank tank;
    private String tankNo;
    private WashingPlatform platform;
    private String platformNo;
    private WashingNozzle nozzle;
    private String nozzleNo;
    private AutomatShift shift;
    private String shiftNo;
    private Account account;
    private Date saleDateTime;
    private Stock stock;
    private BigDecimal unitPrice;
    private BigDecimal operationAmount;
    private BigDecimal taxRate;
    private BigDecimal taxPrice;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalMoney;
    private String description;
    private int paymentType;
    private Currency currency;
    private BigDecimal exchangeRate;
    private boolean isOnline;
    private int operationTime;
    private String customerRfid;
    private String barcodeNo;
    private String mobileNo;
    private BigDecimal expenseUnitPrice;
    private Currency expenseCurrency;
    private BigDecimal expenseExchangeRate;
    private BigDecimal electricAmount;
    private BigDecimal electricUnitPrice;

    public AutomatSales() {
        this.washingMachine = new WashingMachicne();
        this.shift = new AutomatShift();
        this.account = new Account();
        this.currency = new Currency();
        this.platform = new WashingPlatform();
        this.tank = new WashingTank();
        this.nozzle = new WashingNozzle();
        this.stock = new Stock();
        this.expenseCurrency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WashingMachicne getWashingMachine() {
        return washingMachine;
    }

    public void setWashingMachine(WashingMachicne washingMachine) {
        this.washingMachine = washingMachine;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public WashingTank getTank() {
        return tank;
    }

    public void setTank(WashingTank tank) {
        this.tank = tank;
    }

    public String getTankNo() {
        return tankNo;
    }

    public void setTankNo(String tankNo) {
        this.tankNo = tankNo;
    }

    public WashingPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(WashingPlatform platform) {
        this.platform = platform;
    }

    public String getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(String platformNo) {
        this.platformNo = platformNo;
    }

    public WashingNozzle getNozzle() {
        return nozzle;
    }

    public void setNozzle(WashingNozzle nozzle) {
        this.nozzle = nozzle;
    }

    public String getNozzleNo() {
        return nozzleNo;
    }

    public void setNozzleNo(String nozzleNo) {
        this.nozzleNo = nozzleNo;
    }

    public AutomatShift getShift() {
        return shift;
    }

    public void setShift(AutomatShift shift) {
        this.shift = shift;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getSaleDateTime() {
        return saleDateTime;
    }

    public void setSaleDateTime(Date saleDateTime) {
        this.saleDateTime = saleDateTime;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(BigDecimal operationAmount) {
        this.operationAmount = operationAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(BigDecimal taxPrice) {
        this.taxPrice = taxPrice;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(int operationTime) {
        this.operationTime = operationTime;
    }

    public String getCustomerRfid() {
        return customerRfid;
    }

    public void setCustomerRfid(String customerRfid) {
        this.customerRfid = customerRfid;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public BigDecimal getExpenseUnitPrice() {
        return expenseUnitPrice;
    }

    public void setExpenseUnitPrice(BigDecimal expenseUnitPrice) {
        this.expenseUnitPrice = expenseUnitPrice;
    }

    public Currency getExpenseCurrency() {
        return expenseCurrency;
    }

    public void setExpenseCurrency(Currency expenseCurrency) {
        this.expenseCurrency = expenseCurrency;
    }

    public BigDecimal getExpenseExchangeRate() {
        return expenseExchangeRate;
    }

    public void setExpenseExchangeRate(BigDecimal expenseExchangeRate) {
        this.expenseExchangeRate = expenseExchangeRate;
    }

    public BigDecimal getElectricAmount() {
        return electricAmount;
    }

    public void setElectricAmount(BigDecimal electricAmount) {
        this.electricAmount = electricAmount;
    }

    public BigDecimal getElectricUnitPrice() {
        return electricUnitPrice;
    }

    public void setElectricUnitPrice(BigDecimal electricUnitPrice) {
        this.electricUnitPrice = electricUnitPrice;
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

/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:28:55
 */
package com.mepsan.marwiz.general.model.automation;

import com.mepsan.marwiz.general.model.finance.Credit;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Unit;
import java.math.BigDecimal;
import java.util.Date;

public class FuelShiftSales {

    private int id;
    private FuelShift fuelShift;
    private Date processDate;
    private String pumpno;
    private String nozzleNo;
    private String stockName;
    private String stockCode;
    private BigDecimal liter;
    private BigDecimal price;
    private BigDecimal discountTotal;
    private BigDecimal totalMoney;
    private String plate;
    private String attendant;
    private String attendantCode;
    private int salteType;
    private int paymentType;
    private FuelSaleType fuelSaleType;
    private String accountCode;
    private String receiptNo;
    private String processDateString;

    private int saleCount;//tabloda yok sayfada göstermek için ekledik
    private Account account;
    private Unit unit;
    private Credit credit;

    private boolean isErrorAttendant;
    private boolean isErrorStock;
    private boolean isErrorAccount;
    private boolean isErrorNozzle;
    private boolean isSaleType;
    private boolean isCardType;

    private boolean isConnectVehicle;
    private int fuelCardType;
    private BigDecimal redemption;

    public FuelShiftSales() {
        this.fuelShift = new FuelShift();
        this.fuelSaleType = new FuelSaleType();
        this.account = new Account();
        this.unit = new Unit();
        this.credit = new Credit();
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public FuelSaleType getFuelSaleType() {
        return fuelSaleType;
    }

    public void setFuelSaleType(FuelSaleType fuelSaleType) {
        this.fuelSaleType = fuelSaleType;
    }

    public String getAttendantCode() {
        return attendantCode;
    }

    public void setAttendantCode(String attendantCode) {
        this.attendantCode = attendantCode;
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

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getPumpno() {
        return pumpno;
    }

    public void setPumpno(String pumpno) {
        this.pumpno = pumpno;
    }

    public String getNozzleNo() {
        return nozzleNo;
    }

    public void setNozzleNo(String nozzleNo) {
        this.nozzleNo = nozzleNo;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getLiter() {
        return liter;
    }

    public void setLiter(BigDecimal liter) {
        this.liter = liter;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public int getSalteType() {
        return salteType;
    }

    public void setSalteType(int salteType) {
        this.salteType = salteType;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public boolean isIsErrorAttendant() {
        return isErrorAttendant;
    }

    public void setIsErrorAttendant(boolean isErrorAttendant) {
        this.isErrorAttendant = isErrorAttendant;
    }

    public boolean isIsErrorStock() {
        return isErrorStock;
    }

    public void setIsErrorStock(boolean isErrorStock) {
        this.isErrorStock = isErrorStock;
    }

    public boolean isIsErrorAccount() {
        return isErrorAccount;
    }

    public void setIsErrorAccount(boolean isErrorAccount) {
        this.isErrorAccount = isErrorAccount;
    }

    public boolean isIsErrorNozzle() {
        return isErrorNozzle;
    }

    public void setIsErrorNozzle(boolean isErrorNozzle) {
        this.isErrorNozzle = isErrorNozzle;
    }

    public String getProcessDateString() {
        return processDateString;
    }

    public void setProcessDateString(String processDateString) {
        this.processDateString = processDateString;
    }

    public boolean isIsSaleType() {
        return isSaleType;
    }

    public void setIsSaleType(boolean isSaleType) {
        this.isSaleType = isSaleType;
    }

    public boolean isIsConnectVehicle() {
        return isConnectVehicle;
    }

    public void setIsConnectVehicle(boolean isConnectVehicle) {
        this.isConnectVehicle = isConnectVehicle;
    }

    public int getFuelCardType() {
        return fuelCardType;
    }

    public void setFuelCardType(int fuelCardType) {
        this.fuelCardType = fuelCardType;
    }

    public boolean isIsCardType() {
        return isCardType;
    }

    public void setIsCardType(boolean isCardType) {
        this.isCardType = isCardType;
    }

    public BigDecimal getRedemption() {
        return redemption;
    }

    public void setRedemption(BigDecimal redemption) {
        this.redemption = redemption;
    }
    

    @Override
    public String toString() {
        return this.getFuelShift().getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.02.2018 11:00:56
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Receipt;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class Sales extends WotLogging {

    private int id;
    private Date processDate;
    private Shift shift;
    private String shiftNo;
    private Account account;
    private Invoice invoice;
    private Receipt receipt;
    private PointOfSale pointOfSale;
    private String posMacAddress;
    private UserData user;
    private String userName;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private BigDecimal totalPrice;
    private BigDecimal totalMoney;
    private boolean isReturn;
    private Currency currency;
    private Sales returnParentId;
    private Type saleType;
    private Type discountType;
    private String transactionNo;
    private boolean isOnline;
    private BigDecimal discountPrice;

    public Sales() {
        this.shift = new Shift();
        this.account = new Account();
        this.invoice = new Invoice();
        this.receipt = new Receipt();
        this.pointOfSale = new PointOfSale();
        this.currency = new Currency();
        this.user = new UserData();
        this.saleType = new Type();
        this.discountType = new Type();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public PointOfSale getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(PointOfSale pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getPosMacAddress() {
        return posMacAddress;
    }

    public void setPosMacAddress(String posMacAddress) {
        this.posMacAddress = posMacAddress;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isIsReturn() {
        return isReturn;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
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

    public void setIsReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }

    public Sales getReturnParentId() {
        return returnParentId;
    }

    public void setReturnParentId(Sales returnParentId) {
        this.returnParentId = returnParentId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getSaleType() {
        return saleType;
    }

    public void setSaleType(Type saleType) {
        this.saleType = saleType;
    }

    public Type getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Type discountType) {
        this.discountType = discountType;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
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

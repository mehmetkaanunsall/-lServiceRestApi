/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2017 09:37:40
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Size;

public class Invoice extends WotLogging implements Comparable<Invoice> {

    private int id;
    private boolean isPurchase;
    private Account account;
    @Size(max = 60)
    private String documentNumber;
    private Date dueDate;
    private Date invoiceDate;
    private Date dispatchDate;
    private String dispatchAddress;
    private String description;
    private Status status;
    private Type type;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private BigDecimal totalPrice;
    private BigDecimal totalMoney;
    private BigDecimal remainingMoney;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal roundingPrice;
    private boolean isDiscountRate;
    private Warehouse warehouse;

    private String deliveryPerson;
    private DocumentNumber dNumber;
    private String documentSerial;
    private boolean isPeriodInvoice;//periyodik fatura olup olmadıgını tutar.
    private boolean isPayment;//ödemesi var mı faturanın 

    private int saleId;
    private int posId;//satış pos dan mı yapılmıs kontrol etmek ıcın

    private String warehouseIdList;
    private List<Warehouse> listOfWarehouse;
    private String jsonWarehouses;
    private BigDecimal totalProfit;

    private int taxPayerTypeId;
    private int deliveryTypeId;
    private int invoiceScenarioId;
    private boolean isEInvoice;
    private BranchSetting branchSetting;
    private Invoice priceDifferenceInvoice;
    private boolean isDifferenceDirection;
    private BigDecimal priceDifferenceTotalMoney;
    private boolean isWait;
    private String waitInvoiceItemJson;
    private boolean sapLogIsSend; //Fatura Sap'ye başarılı olarak gönderilmiş mi kontrol etmek için
    private boolean sapIsSendWaybill; //İrsaliyeli fatura Sap'ye gönderildiğinde Sap'de irsaliyenin başarılı olarak oluşup oluşmadığının bilgisini tutar
    private boolean isOrderConnection;
    private String orderIds;
    private boolean isFuel;
    private BigDecimal priceDifferenceTotalPrice;
    
    public Invoice() {
        this.account = new Account();
        this.type = new Type();
        this.currency = new Currency();
        this.status = new Status();
        this.warehouse = new Warehouse();
        this.dNumber = new DocumentNumber();
        this.listOfWarehouse = new ArrayList<>();
        this.branchSetting = new BranchSetting();
    }

    public boolean isIsEInvoice() {
        return isEInvoice;
    }

    public void setIsEInvoice(boolean isEInvoice) {
        this.isEInvoice = isEInvoice;
    }

    public int getInvoiceScenarioId() {
        return invoiceScenarioId;
    }

    public void setInvoiceScenarioId(int invoiceScenarioId) {
        this.invoiceScenarioId = invoiceScenarioId;
    }

    public int getTaxPayerTypeId() {
        return taxPayerTypeId;
    }

    public void setTaxPayerTypeId(int taxPayerTypeId) {
        this.taxPayerTypeId = taxPayerTypeId;
    }

    public int getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(int deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public boolean isIsDiscountRate() {
        return isDiscountRate;
    }

    public void setIsDiscountRate(boolean isDiscountRate) {
        this.isDiscountRate = isDiscountRate;
    }

    public BigDecimal getRoundingPrice() {
        return roundingPrice;
    }

    public void setRoundingPrice(BigDecimal roundingPrice) {
        this.roundingPrice = roundingPrice;
    }

    public boolean isIsPeriodInvoice() {
        return isPeriodInvoice;
    }

    public void setIsPeriodInvoice(boolean isPeriodInvoice) {
        this.isPeriodInvoice = isPeriodInvoice;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public BigDecimal getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(BigDecimal remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public DocumentNumber getdNumber() {
        return dNumber;
    }

    public String getDocumentSerial() {
        return documentSerial;
    }

    public void setDocumentSerial(String documentSerial) {
        this.documentSerial = documentSerial;
    }

    public void setdNumber(DocumentNumber dNumber) {
        this.dNumber = dNumber;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Invoice(int id) {
        this.id = id;
    }

    public String getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(String deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getDispatchAddress() {
        return dispatchAddress;
    }

    public void setDispatchAddress(String dispatchAddress) {
        this.dispatchAddress = dispatchAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getWarehouseIdList() {
        return warehouseIdList;
    }

    public void setWarehouseIdList(String warehouseIdList) {
        this.warehouseIdList = warehouseIdList;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public String getJsonWarehouses() {
        return jsonWarehouses;
    }

    public void setJsonWarehouses(String jsonWarehouses) {
        this.jsonWarehouses = jsonWarehouses;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public boolean isIsPayment() {
        return isPayment;
    }

    public void setIsPayment(boolean isPayment) {
        this.isPayment = isPayment;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public boolean isIsDifferenceDirection() {
        return isDifferenceDirection;
    }

    public void setIsDifferenceDirection(boolean isDifferenceDirection) {
        this.isDifferenceDirection = isDifferenceDirection;
    }

    public Invoice getPriceDifferenceInvoice() {
        return priceDifferenceInvoice;
    }

    public void setPriceDifferenceInvoice(Invoice priceDifferenceInvoice) {
        this.priceDifferenceInvoice = priceDifferenceInvoice;
    }

    public BigDecimal getPriceDifferenceTotalMoney() {
        return priceDifferenceTotalMoney;
    }

    public void setPriceDifferenceTotalMoney(BigDecimal priceDifferenceTotalMoney) {
        this.priceDifferenceTotalMoney = priceDifferenceTotalMoney;
    }

    public boolean isIsWait() {
        return isWait;
    }

    public void setIsWait(boolean isWait) {
        this.isWait = isWait;
    }

    public String getWaitInvoiceItemJson() {
        return waitInvoiceItemJson;
    }

    public void setWaitInvoiceItemJson(String waitInvoiceItemJson) {
        this.waitInvoiceItemJson = waitInvoiceItemJson;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }

    public boolean isIsOrderConnection() {
        return isOrderConnection;
    }

    public void setIsOrderConnection(boolean isOrderConnection) {
        this.isOrderConnection = isOrderConnection;
    }

    @Override
    public String toString() {
        if (this.documentSerial != null) {
            return this.documentSerial + this.getDocumentNumber();
        } else {
            return this.getDocumentNumber();
        }
    }

    public boolean isSapLogIsSend() {
        return sapLogIsSend;
    }

    public void setSapLogIsSend(boolean sapLogIsSend) {
        this.sapLogIsSend = sapLogIsSend;
    }

    public boolean isSapIsSendWaybill() {
        return sapIsSendWaybill;
    }

    public void setSapIsSendWaybill(boolean sapIsSendWaybill) {
        this.sapIsSendWaybill = sapIsSendWaybill;
    }

    public boolean isIsFuel() {
        return isFuel;
    }

    public void setIsFuel(boolean isFuel) {
        this.isFuel = isFuel;
    }

    public BigDecimal getPriceDifferenceTotalPrice() {
        return priceDifferenceTotalPrice;
    }

    public void setPriceDifferenceTotalPrice(BigDecimal priceDifferenceTotalPrice) {
        this.priceDifferenceTotalPrice = priceDifferenceTotalPrice;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public int compareTo(Invoice o) {
        return getInvoiceDate().compareTo(o.getInvoiceDate());
    }
}

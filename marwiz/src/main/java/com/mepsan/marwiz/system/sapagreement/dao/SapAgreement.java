package com.mepsan.marwiz.system.sapagreement.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.PaymentType;
import com.mepsan.marwiz.general.model.general.ZSeries;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author elif.mart
 */
public class SapAgreement {

    private int id;
    private Branch branch;
    private int period;
    private Date processDate;
    private String automationJson;
    private String posSaleJson;
    private String expenseJson;
    private String exchangeJson;
    private String fuelZJson;
    private String marketZJson;
    private String totalJson;
    private String safeTransferJson;
    private String bankTransferJson;
    private String sendData;
    private Boolean isSend;
    private Date sendDate;
    private int sendCount;
    private String response;
    private String documentNumber;

    private String message; //Web servisten veri alma işleminde dönen mesaj 
    private String messageType;//Web servisten dönen response içerisindeki mesaj tipi bilgisi
    private boolean isSuccess;//Web servisten veri alma işleminin başarılı olup olmadığının tutar
    private ZSeries zSeries;
    private String expenseDefinition;
    private int expensecode;

    private Stock stock;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalMoney;
    private String description;
    private Exchange exchange;
    private String bankName;
    private BigDecimal merchantNumber;
    private String posId;
    private BigDecimal posDailyEnd;
    private BigDecimal difference;
    private Currency currency;
    private PaymentType paymentType;
    private BigDecimal endDayTotal;
    private BigDecimal automationDiffAmount;
    private BigDecimal transferAutomationDiffAmount;
    private BigDecimal totalFuelLiter;
    private BigDecimal totalMarket;
    private BigDecimal marketDiffAmount;
    private BigDecimal transferMarketDiffAmount;
    private boolean isTestSale;
    private BigDecimal returnSalesTotal;
    private BigDecimal returnsWithSale;

    public SapAgreement() {
        this.branch = new Branch();
        this.stock = new Stock();
        this.exchange = new Exchange();
        this.zSeries = new ZSeries();
        this.currency = new Currency();
        this.paymentType = new PaymentType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getAutomationJson() {
        return automationJson;
    }

    public void setAutomationJson(String automationJson) {
        this.automationJson = automationJson;
    }

    public String getPosSaleJson() {
        return posSaleJson;
    }

    public void setPosSaleJson(String posSaleJson) {
        this.posSaleJson = posSaleJson;
    }

    public String getExpenseJson() {
        return expenseJson;
    }

    public void setExpenseJson(String expenseJson) {
        this.expenseJson = expenseJson;
    }

    public String getExchangeJson() {
        return exchangeJson;
    }

    public void setExchangeJson(String exchangeJson) {
        this.exchangeJson = exchangeJson;
    }

    public String getFuelZJson() {
        return fuelZJson;
    }

    public void setFuelZJson(String fuelZJson) {
        this.fuelZJson = fuelZJson;
    }

    public String getMarketZJson() {
        return marketZJson;
    }

    public void setMarketZJson(String marketZJson) {
        this.marketZJson = marketZJson;
    }

    public String getTotalJson() {
        return totalJson;
    }

    public void setTotalJson(String totalJson) {
        this.totalJson = totalJson;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getMerchantNumber() {
        return merchantNumber;
    }

    public void setMerchantNumber(BigDecimal merchantNumber) {
        this.merchantNumber = merchantNumber;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public BigDecimal getPosDailyEnd() {
        return posDailyEnd;
    }

    public void setPosDailyEnd(BigDecimal posDailyEnd) {
        this.posDailyEnd = posDailyEnd;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getSafeTransferJson() {
        return safeTransferJson;
    }

    public void setSafeTransferJson(String safeTransferJson) {
        this.safeTransferJson = safeTransferJson;
    }

    public String getBankTransferJson() {
        return bankTransferJson;
    }

    public void setBankTransferJson(String bankTransferJson) {
        this.bankTransferJson = bankTransferJson;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ZSeries getzSeries() {
        return zSeries;
    }

    public void setzSeries(ZSeries zSeries) {
        this.zSeries = zSeries;
    }

    public String getExpenseDefinition() {
        return expenseDefinition;
    }

    public void setExpenseDefinition(String expenseDefinition) {
        this.expenseDefinition = expenseDefinition;
    }

    public int getExpensecode() {
        return expensecode;
    }

    public void setExpensecode(int expensecode) {
        this.expensecode = expensecode;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getEndDayTotal() {
        return endDayTotal;
    }

    public void setEndDayTotal(BigDecimal endDayTotal) {
        this.endDayTotal = endDayTotal;
    }

    public BigDecimal getAutomationDiffAmount() {
        return automationDiffAmount;
    }

    public void setAutomationDiffAmount(BigDecimal automationDiffAmount) {
        this.automationDiffAmount = automationDiffAmount;
    }

    public BigDecimal getTransferAutomationDiffAmount() {
        return transferAutomationDiffAmount;
    }

    public void setTransferAutomationDiffAmount(BigDecimal transferAutomationDiffAmount) {
        this.transferAutomationDiffAmount = transferAutomationDiffAmount;
    }

    public BigDecimal getTotalFuelLiter() {
        return totalFuelLiter;
    }

    public void setTotalFuelLiter(BigDecimal totalFuelLiter) {
        this.totalFuelLiter = totalFuelLiter;
    }

    public BigDecimal getTotalMarket() {
        return totalMarket;
    }

    public void setTotalMarket(BigDecimal totalMarket) {
        this.totalMarket = totalMarket;
    }

    public BigDecimal getMarketDiffAmount() {
        return marketDiffAmount;
    }

    public void setMarketDiffAmount(BigDecimal marketDiffAmount) {
        this.marketDiffAmount = marketDiffAmount;
    }

    public BigDecimal getTransferMarketDiffAmount() {
        return transferMarketDiffAmount;
    }

    public void setTransferMarketDiffAmount(BigDecimal transferMarketDiffAmount) {
        this.transferMarketDiffAmount = transferMarketDiffAmount;
    }

    public boolean isIsTestSale() {
        return isTestSale;
    }

    public void setIsTestSale(boolean isTestSale) {
        this.isTestSale = isTestSale;
    }

    public BigDecimal getReturnSalesTotal() {
        return returnSalesTotal;
    }

    public void setReturnSalesTotal(BigDecimal returnSalesTotal) {
        this.returnSalesTotal = returnSalesTotal;
    }

    public BigDecimal getReturnsWithSale() {
        return returnsWithSale;
    }

    public void setReturnsWithSale(BigDecimal returnsWithSale) {
        this.returnsWithSale = returnsWithSale;
    }

}

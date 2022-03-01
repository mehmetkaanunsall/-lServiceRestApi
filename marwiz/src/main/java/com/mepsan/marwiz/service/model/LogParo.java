/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 08:15:32
 */
package com.mepsan.marwiz.service.model;

import java.util.Date;

public class LogParo {

    private int id;
    private long requestId;
    private String transactionNo;
    private String provisionNo;
    private int typeId;
    private int saleId;
    private boolean isParoCustomer;
    private String sendData;
    private boolean isSuccess;
    private int errorCode;
    private String errorMessage;
    private boolean isSend;
    private Date sendBeginDate;
    private Date sendEndDate;
    private int sendCount;
    private int errorCount;
    private String response;
    //Veri tabanında paro ödeme işlemi olmayan satış başlatmaları işleme almaz.
    private boolean isThereParoPayment;
    // Satış başlatma gönderildikten sonra ödeme işlemi göderileceği için ödeme işlemi gönderilen satışın kaydını günceller
    private boolean isUpdated;
    private int createdId;
    private int branchId;
    private boolean isQRCode;
    private boolean isInvoice;
    private int pointOfSaleId;
    private String orderId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public boolean isIsParoCustomer() {
        return isParoCustomer;
    }

    public void setIsParoCustomer(boolean isParoCustomer) {
        this.isParoCustomer = isParoCustomer;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public Date getSendBeginDate() {
        return sendBeginDate;
    }

    public void setSendBeginDate(Date sendBeginDate) {
        this.sendBeginDate = sendBeginDate;
    }

    public Date getSendEndDate() {
        return sendEndDate;
    }

    public void setSendEndDate(Date sendEndDate) {
        this.sendEndDate = sendEndDate;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public boolean isIsThereParoPayment() {
        return isThereParoPayment;
    }

    public void setIsThereParoPayment(boolean isThereParoPayment) {
        this.isThereParoPayment = isThereParoPayment;
    }

    public boolean isIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getCreatedId() {
        return createdId;
    }

    public void setCreatedId(int createdId) {
        this.createdId = createdId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getProvisionNo() {
        return provisionNo;
    }

    public void setProvisionNo(String provisionNo) {
        this.provisionNo = provisionNo;
    }

    public boolean isIsQRCode() {
        return isQRCode;
    }

    public void setIsQRCode(boolean isQRCode) {
        this.isQRCode = isQRCode;
    }

    public boolean isIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    public int getPointOfSaleId() {
        return pointOfSaleId;
    }

    public void setPointOfSaleId(int pointOfSaleId) {
        this.pointOfSaleId = pointOfSaleId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}

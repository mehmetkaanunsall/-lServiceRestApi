package com.mepsan.marwiz.system.sapintegration.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSapResponseDetail;

/**
 *
 * @author elif.mart
 */
public class IntegrationForSap {

    private int id;
    private String jsonData;
    private int event;
    private Date sendDate;
    private String response;
    private int sendCount;
    private boolean isSend;
    private int typeId;
    private String type;
    private Date processDate;
    private String documentNumber;
    private String sapDocumentNumber;
    private String message;
    private String reasonForSending;
    private boolean isRetail; //sap_saleinvoice tablosu için
    private String invoiceNumber;
    private BigDecimal totalMoney;
    private String description;
    private String sapIDocNo;
    private boolean isDirection;
    private int objectId;
    private String accountName;
    private boolean isSendWaybill;
    private List<IntegrationForSapResponseDetail> listOfResponseDetails;

    public IntegrationForSap() {
        this.listOfResponseDetails = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReasonForSending() {
        return reasonForSending;
    }

    public void setReasonForSending(String reasonForSending) {
        this.reasonForSending = reasonForSending;
    }

    public String getSapDocumentNumber() {
        return sapDocumentNumber;
    }

    public void setSapDocumentNumber(String sapDocumentNumber) {
        this.sapDocumentNumber = sapDocumentNumber;
    }

    public boolean isIsRetail() {
        return isRetail;
    }

    public void setIsRetail(boolean isRetail) {
        this.isRetail = isRetail;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getSapIDocNo() {
        return sapIDocNo;
    }

    public void setSapIDocNo(String sapIDocNo) {
        this.sapIDocNo = sapIDocNo;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isIsSendWaybill() {
        return isSendWaybill;
    }

    public void setIsSendWaybill(boolean isSendWaybill) {
        this.isSendWaybill = isSendWaybill;
    }

    public List<IntegrationForSapResponseDetail> getListOfResponseDetails() {
        return listOfResponseDetails;
    }

    public void setListOfResponseDetails(List<IntegrationForSapResponseDetail> listOfResponseDetails) {
        this.listOfResponseDetails = listOfResponseDetails;
    }

}

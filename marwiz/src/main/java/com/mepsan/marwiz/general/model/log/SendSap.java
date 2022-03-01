/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 30.04.2019 14:48:55
 */
package com.mepsan.marwiz.general.model.log;

import java.util.Date;

public class SendSap implements Cloneable {

    private int id;
    private int financingDocumentId;
    private Date sendBeginDate;
    private Date sendEndDate;
    private String sendData;
    private int branchId;
    private String branchCode;
    private boolean isSend;
    private int sendCount;

    private String type;
    private int errorNumber;
    private String message;
    private String documentNo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFinancingDocumentId() {
        return financingDocumentId;
    }

    public void setFinancingDocumentId(int financingDocumentId) {
        this.financingDocumentId = financingDocumentId;
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

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

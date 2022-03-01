package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;

/**
 *
 * @author elif.mart
 */
public class SendEInvoice implements Cloneable {

    private int id;
    private int invoiceId;
    private String sendData;
    private boolean isSend;
    private Date sendBeginDate;
    private Date sendEndDate;
    private int sendCount;
    private String responseCode;
    private String responseDescription;
    private String gibInvoice;
    private String integrationInvoice;
    private int invoiceStatus;
    private Branch branch;

    public SendEInvoice() {

        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
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

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getGibInvoice() {
        return gibInvoice;
    }

    public void setGibInvoice(String gibInvoice) {
        this.gibInvoice = gibInvoice;
    }

    public String getIntegrationInvoice() {
        return integrationInvoice;
    }

    public void setIntegrationInvoice(String integrationInvoice) {
        this.integrationInvoice = integrationInvoice;
    }

    public int getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(int invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}

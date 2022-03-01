package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public class EInvoice extends Invoice {

    private String oldAccountName;
    private int approvalStatusId;
    private String approvalDescription;
    private String referenceNumber;
    private Boolean isSend;
    private Date endDate;
    private Date beginDate;
    private SendEInvoice sendEInvoice;
    private String invoiceItemString;
    private List<InvoiceItem> listInvoiceItem;
    private Date receivedDate; // Faturanın e-fatura sistemine gönderildiği tarih
    private String oldTaxNo;
    private boolean isArchive;
    

    public EInvoice() {

        this.sendEInvoice = new SendEInvoice();
        listInvoiceItem = new ArrayList<>();

    }

    public List<InvoiceItem> getListInvoiceItem() {
        return listInvoiceItem;
    }

    public void setListInvoiceItem(List<InvoiceItem> listInvoiceItem) {
        this.listInvoiceItem = listInvoiceItem;
    }

    public String getInvoiceItemString() {
        return invoiceItemString;
    }

    public void setInvoiceItemString(String invoiceItemString) {
        this.invoiceItemString = invoiceItemString;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public SendEInvoice getSendEInvoice() {
        return sendEInvoice;
    }

    public void setSendEInvoice(SendEInvoice sendEInvoice) {
        this.sendEInvoice = sendEInvoice;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getApprovalDescription() {
        return approvalDescription;
    }

    public void setApprovalDescription(String approvalDescription) {
        this.approvalDescription = approvalDescription;
    }

    public int getApprovalStatusId() {
        return approvalStatusId;
    }

    public void setApprovalStatusId(int approvalStatusId) {
        this.approvalStatusId = approvalStatusId;
    }

    public String getOldAccountName() {
        return oldAccountName;
    }

    public void setOldAccountName(String oldAccountName) {
        this.oldAccountName = oldAccountName;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getOldTaxNo() {
        return oldTaxNo;
    }

    public void setOldTaxNo(String oldTaxNo) {
        this.oldTaxNo = oldTaxNo;
    }

    public boolean isIsArchive() {
        return isArchive;
    }

    public void setIsArchive(boolean isArchive) {
        this.isArchive = isArchive;
    }

}

/**
 *
 * @author elif.mart
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;

public class IncomingEInvoice {

    private int id;
    private int invoiceId;
    private String getData;
    private Date processDate;
    private boolean isSuccess;
    private String responseCode;
    private String responseDescription;
    private int approvalStatusId;
    private String approvalDescription;
    private int requestId;
    private String gibInvoice;
    private Date gibDate;
    private Branch branch;
    private String gibAccountName;
    private String gibTaxNo;
    private Date invoiceDate;

    public IncomingEInvoice() {
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

    public String getGetData() {
        return getData;
    }

    public void setGetData(String getData) {
        this.getData = getData;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
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

    public int getApprovalStatusId() {
        return approvalStatusId;
    }

    public void setApprovalStatusId(int approvalStatusId) {
        this.approvalStatusId = approvalStatusId;
    }

    public String getApprovalDescription() {
        return approvalDescription;
    }

    public void setApprovalDescription(String approvalDescription) {
        this.approvalDescription = approvalDescription;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getGibInvoice() {
        return gibInvoice;
    }

    public void setGibInvoice(String gibInvoice) {
        this.gibInvoice = gibInvoice;
    }

    public Date getGibDate() {
        return gibDate;
    }

    public void setGibDate(Date gibDate) {
        this.gibDate = gibDate;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getGibAccountName() {
        return gibAccountName;
    }

    public void setGibAccountName(String gibAccountName) {
        this.gibAccountName = gibAccountName;
    }

    public String getGibTaxNo() {
        return gibTaxNo;
    }

    public void setGibTaxNo(String gibTaxNo) {
        this.gibTaxNo = gibTaxNo;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

}

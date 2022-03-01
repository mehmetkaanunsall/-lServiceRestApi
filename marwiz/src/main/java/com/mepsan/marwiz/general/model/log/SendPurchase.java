/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 09:01:12 
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.Date;

public class SendPurchase {
    private int id;
    private int invoiceId;
    private String senddata;
    private boolean issend;
    private Date sendbegindate;
    private Date sendenddate;
    private int sendcount;
    private String response;
    private BranchSetting branchSetting;
    private String licenceCode;

    public SendPurchase() {
        branchSetting = new BranchSetting();
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

    public String getSenddata() {
        return senddata;
    }

    public void setSenddata(String senddata) {
        this.senddata = senddata;
    }

    public boolean isIssend() {
        return issend;
    }

    public void setIssend(boolean issend) {
        this.issend = issend;
    }

    public Date getSendbegindate() {
        return sendbegindate;
    }

    public void setSendbegindate(Date sendbegindate) {
        this.sendbegindate = sendbegindate;
    }

    public Date getSendenddate() {
        return sendenddate;
    }

    public void setSendenddate(Date sendenddate) {
        this.sendenddate = sendenddate;
    }

    public int getSendcount() {
        return sendcount;
    }

    public void setSendcount(int sendcount) {
        this.sendcount = sendcount;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public String getLicenceCode() {
        return licenceCode;
    }

    public void setLicenceCode(String licenceCode) {
        this.licenceCode = licenceCode;
    }
    
}

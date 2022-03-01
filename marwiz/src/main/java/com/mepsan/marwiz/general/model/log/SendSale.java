/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 17:15:08
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

public class SendSale extends WotLogging {

    private int id;
    private int saleId;
    private String senddata;
    private boolean issend;
    private Date sendbegindate;
    private Date sendenddate;
    private int sendcount;
    private String response;
    private BranchSetting branchSetting;
    private String licenceCode;

    public SendSale() {
        branchSetting = new BranchSetting();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.02.2019 08:59:13
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

public class SendPriceChangeRequest extends WotLogging {

    private int id;
    private int priceChangeRequestId;
    private String senddata;
    private boolean isSend;
    private Date sendbegindate;
    private Date sendenddate;
    private int sendcount;
    private String response;
    private BranchSetting branchSetting;

    public SendPriceChangeRequest() {
        branchSetting= new BranchSetting();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriceChangeRequestId() {
        return priceChangeRequestId;
    }

    public void setPriceChangeRequestId(int priceChangeRequestId) {
        this.priceChangeRequestId = priceChangeRequestId;
    }

    public String getSenddata() {
        return senddata;
    }

    public void setSenddata(String senddata) {
        this.senddata = senddata;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
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

}

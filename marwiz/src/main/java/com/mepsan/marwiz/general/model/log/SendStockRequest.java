/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:52:13
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

public class SendStockRequest extends WotLogging {

    private int id;
    private int stockrequestId;
    private String senddata;
    private boolean isSend;
    private Date sendbegindate;
    private Date sendenddate;
    private int sendcount;
    private String response;
    private BranchSetting branchSetting;

    public SendStockRequest() {
        branchSetting= new BranchSetting();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockrequestId() {
        return stockrequestId;
    }

    public void setStockrequestId(int stockrequestId) {
        this.stockrequestId = stockrequestId;
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

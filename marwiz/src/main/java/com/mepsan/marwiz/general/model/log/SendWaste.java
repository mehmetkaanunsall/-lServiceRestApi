/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.08.2019 08:35:47
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.Date;

public class SendWaste {

    private int id;
    private BranchSetting branchSetting;
    private int warehouseReceiptId;
    private String sendData;
    private boolean isSend;
    private Date sendBeginDate;
    private Date sendEndDate;
    private int sendCount;
    private String response;

    public SendWaste() {
        this.branchSetting = new BranchSetting();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public int getWarehouseReceiptId() {
        return warehouseReceiptId;
    }

    public void setWarehouseReceiptId(int warehouseReceiptId) {
        this.warehouseReceiptId = warehouseReceiptId;
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}

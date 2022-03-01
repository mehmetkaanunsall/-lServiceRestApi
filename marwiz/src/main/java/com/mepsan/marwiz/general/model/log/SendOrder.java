/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.Date;

/**
 *
 * @author esra.cabuk
 */
public class SendOrder {
    
    private int id;
    private int orderId;
    private String senddata;
    private boolean issend;
    private Date sendbegindate;
    private Date sendenddate;
    private int sendcount;
    private String response;
    private BranchSetting branchSetting;
    private String licenceCode;

    public SendOrder() {
        branchSetting = new BranchSetting();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

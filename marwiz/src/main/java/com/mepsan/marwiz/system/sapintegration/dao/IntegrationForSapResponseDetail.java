/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.dao;

/**
 *
 * @author elif.mart
 */
public class IntegrationForSapResponseDetail {

    private int id;
    private boolean isSend;
    private String sapDocumentNumber;
    private String message;
    private String sapIDocNo;

    public IntegrationForSapResponseDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSapDocumentNumber() {
        return sapDocumentNumber;
    }

    public void setSapDocumentNumber(String sapDocumentNumber) {
        this.sapDocumentNumber = sapDocumentNumber;
    }

    public String getSapIDocNo() {
        return sapIDocNo;
    }

    public void setSapIDocNo(String sapIDocNo) {
        this.sapIDocNo = sapIDocNo;
    }

}

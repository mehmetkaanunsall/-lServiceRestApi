/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.dao;

/**
 *
 * @author ali.kurt
 */
public class TotalCount {

    private int deletedCount;
    private int notDeletedCount;
    private int sendCount;
    private int notSendCount;

    public int getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(int deletedCount) {
        this.deletedCount = deletedCount;
    }

    public int getNotDeletedCount() {
        return notDeletedCount;
    }

    public void setNotDeletedCount(int notDeletedCount) {
        this.notDeletedCount = notDeletedCount;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public int getNotSendCount() {
        return notSendCount;
    }

    public void setNotSendCount(int notSendCount) {
        this.notSendCount = notSendCount;
    }

}

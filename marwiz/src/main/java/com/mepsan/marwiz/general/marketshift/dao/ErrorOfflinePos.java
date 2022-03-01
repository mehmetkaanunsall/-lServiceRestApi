package com.mepsan.marwiz.general.marketshift.dao;

import java.math.BigDecimal;

/**
 *
 * @author elif.mart
 */
public class ErrorOfflinePos {

    private int id;
    private String name;
    private String code;
    private int notSendCount;
    private boolean isSuccessful;
    private String localIpAddress;
    private boolean isAccessed;

    public ErrorOfflinePos() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNotSendCount() {
        return notSendCount;
    }

    public void setNotSendCount(int notSendCount) {
        this.notSendCount = notSendCount;
    }

    public boolean isIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public boolean isIsAccessed() {
        return isAccessed;
    }

    public void setIsAccessed(boolean isAccessed) {
        this.isAccessed = isAccessed;
    }

}

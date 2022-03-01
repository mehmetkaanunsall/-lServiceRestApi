/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   10.08.2016 11:52
 *
 * @edited Cihat Küçükbağrıaçık - deviceType eklendi.
 * @edited Zafer Yaşar - deviceMac Kaldırıldı.
 */
package com.mepsan.marwiz.general.model.log_general;

import java.util.Date;

public class UserDataLogin {

    private int id;
    private int userDataId;
    private String ipAddress;
    private String browser;
    private Date loginTime;
    private String location;
    private Boolean success;
    private String deviceType;
    private String deviceName;

    private Date lastFailedLoginTime;
    private Date lastLoginTime;
    private int failedLoginSize;
    private int lockedState;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUserDataId() {
        return userDataId;
    }

    public void setUserDataId(int userDataId) {
        this.userDataId = userDataId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(Date lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }

    public int getLockedState() {
        return lockedState;
    }

    public void setLockedState(int lockedState) {
        this.lockedState = lockedState;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getFailedLoginSize() {
        return failedLoginSize;
    }

    public void setFailedLoginSize(int failedLoginSize) {
        this.failedLoginSize = failedLoginSize;
    }

}

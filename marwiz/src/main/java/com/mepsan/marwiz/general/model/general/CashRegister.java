/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:29:55
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class CashRegister extends WotLogging {

    private int id;
    private String name;
    private Brand brand;
    private String model;
    private String serialNumber;
    private String version;
    private String macAddress;
    private String ipAddress;
    private String port;
    private boolean isExternalEftPos;

    public CashRegister() {
        this.brand = new Brand();
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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isIsExternalEftPos() {
        return isExternalEftPos;
    }

    public void setIsExternalEftPos(boolean isExternalEftPos) {
        this.isExternalEftPos = isExternalEftPos;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

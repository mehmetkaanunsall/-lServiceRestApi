/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:49:16 AM
 */
package com.mepsan.marwiz.general.model.automat;

public class WashingPlatform {

    private int id;
    private WashingMachicne washingMachicne;
    private String platformNo;
    private String description;
    private String port;
    private boolean isActive;
    private String barcodeAddress;
    private String barcodePortNo;
    private int barcodeTimeOut;
    private boolean isActiveBarcode;

    public WashingPlatform() {
        this.washingMachicne = new WashingMachicne();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WashingMachicne getWashingMachicne() {
        return washingMachicne;
    }

    public void setWashingMachicne(WashingMachicne washingMachicne) {
        this.washingMachicne = washingMachicne;
    }

    public String getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(String platformNo) {
        this.platformNo = platformNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getBarcodeAddress() {
        return barcodeAddress;
    }

    public void setBarcodeAddress(String barcodeAddress) {
        this.barcodeAddress = barcodeAddress;
    }

    public String getBarcodePortNo() {
        return barcodePortNo;
    }

    public void setBarcodePortNo(String barcodePortNo) {
        this.barcodePortNo = barcodePortNo;
    }

    public int getBarcodeTimeOut() {
        return barcodeTimeOut;
    }

    public void setBarcodeTimeOut(int barcodeTimeOut) {
        this.barcodeTimeOut = barcodeTimeOut;
    }

    public boolean isIsActiveBarcode() {
        return isActiveBarcode;
    }

    public void setIsActiveBarcode(boolean isActiveBarcode) {
        this.isActiveBarcode = isActiveBarcode;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getPlatformNo();
    }
}

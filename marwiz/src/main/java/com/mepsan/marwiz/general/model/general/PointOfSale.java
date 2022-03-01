/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:19:55
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class PointOfSale extends WotLogging {

    private int id;
    private String name;
    private String code;
    private String brand;
    private String model;
    private Status status;
    private String serialNumber;
    private String version;
    private String softwareVersion;
    private String macAddress;
    private String ipAddress;
    private String port;
    private String integrationCode;
    private String washingMachicneIntegrationCode;
    private CashRegister cashRegister;
    private Warehouse wareHouse;
    private Safe safe;
    private String localIpAddress;
    private boolean isOffline;
    private BigDecimal stockTime;
    private BigDecimal unitTaxTime;
    private BigDecimal userTime;
    private BigDecimal categorizationTime;
    private BigDecimal bankAccountTime;
    private BigDecimal pointOfSaleTime;
    private BigDecimal vendingMachineTime;

    public PointOfSale() {
        this.status = new Status();
        this.cashRegister = new CashRegister();
        this.wareHouse = new Warehouse();
        this.safe = new Safe();
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
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

    public CashRegister getCashRegister() {
        return cashRegister;
    }

    public void setCashRegister(CashRegister cashRegister) {
        this.cashRegister = cashRegister;
    }

    public Warehouse getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(Warehouse wareHouse) {
        this.wareHouse = wareHouse;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public String getIntegrationCode() {
        return integrationCode;
    }

    public void setIntegrationCode(String integrationCode) {
        this.integrationCode = integrationCode;
    }

    public String getWashingMachicneIntegrationCode() {
        return washingMachicneIntegrationCode;
    }

    public void setWashingMachicneIntegrationCode(String washingMachicneIntegrationCode) {
        this.washingMachicneIntegrationCode = washingMachicneIntegrationCode;
    }
    
    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public boolean isIsOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public BigDecimal getStockTime() {
        return stockTime;
    }

    public void setStockTime(BigDecimal stockTime) {
        this.stockTime = stockTime;
    }

    public BigDecimal getUnitTaxTime() {
        return unitTaxTime;
    }

    public void setUnitTaxTime(BigDecimal unitTaxTime) {
        this.unitTaxTime = unitTaxTime;
    }

    public BigDecimal getUserTime() {
        return userTime;
    }

    public void setUserTime(BigDecimal userTime) {
        this.userTime = userTime;
    }

    public BigDecimal getCategorizationTime() {
        return categorizationTime;
    }

    public void setCategorizationTime(BigDecimal categorizationTime) {
        this.categorizationTime = categorizationTime;
    }

    public BigDecimal getBankAccountTime() {
        return bankAccountTime;
    }

    public void setBankAccountTime(BigDecimal bankAccountTime) {
        this.bankAccountTime = bankAccountTime;
    }

    public BigDecimal getPointOfSaleTime() {
        return pointOfSaleTime;
    }

    public void setPointOfSaleTime(BigDecimal pointOfSaleTime) {
        this.pointOfSaleTime = pointOfSaleTime;
    }

    public BigDecimal getVendingMachineTime() {
        return vendingMachineTime;
    }

    public void setVendingMachineTime(BigDecimal vendingMachineTime) {
        this.vendingMachineTime = vendingMachineTime;
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

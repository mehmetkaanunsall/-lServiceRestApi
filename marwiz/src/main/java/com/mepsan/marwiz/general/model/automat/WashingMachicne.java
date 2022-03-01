/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:43:21 AM
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Status;
import java.math.BigDecimal;    

public class WashingMachicne {

    private int id;
    private Branch branch;
    private String code;
    private String name;
    private String macAddress;
    private String ipAddress;
    private String version;
    private String description;
    private Status status;
    private String port;
    private BigDecimal electricUnitPrice;
    private BigDecimal waterUnitPrice;

    public WashingMachicne() {
        this.branch = new Branch();
        this.status = new Status();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public BigDecimal getElectricUnitPrice() {
        return electricUnitPrice;
    }

    public void setElectricUnitPrice(BigDecimal electricUnitPrice) {
        this.electricUnitPrice = electricUnitPrice;
    }

    public BigDecimal getWaterUnitPrice() {
        return waterUnitPrice;
    }

    public void setWaterUnitPrice(BigDecimal waterUnitPrice) {
        this.waterUnitPrice = waterUnitPrice;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getCode();
    }

}

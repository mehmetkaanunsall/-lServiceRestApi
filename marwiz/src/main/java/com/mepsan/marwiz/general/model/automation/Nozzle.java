/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:19:30
 */
package com.mepsan.marwiz.general.model.automation;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class Nozzle extends WotLogging {

    private int id;
    private Warehouse warehouse;
    private String name;
    private String pumpNo;
    private String nozzleNo;
    private Status status;
    private BigDecimal index;
    private boolean isAscending;
    private String description;

    public Nozzle() {
        this.warehouse = new Warehouse();
        this.status = new Status();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPumpNo() {
        return pumpNo;
    }

    public void setPumpNo(String pumpNo) {
        this.pumpNo = pumpNo;
    }

    public String getNozzleNo() {
        return nozzleNo;
    }

    public void setNozzleNo(String nozzleNo) {
        this.nozzleNo = nozzleNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getIndex() {
        return index;
    }

    public void setIndex(BigDecimal index) {
        this.index = index;
    }

    public boolean isIsAscending() {
        return isAscending;
    }

    public void setIsAscending(boolean isAscending) {
        this.isAscending = isAscending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

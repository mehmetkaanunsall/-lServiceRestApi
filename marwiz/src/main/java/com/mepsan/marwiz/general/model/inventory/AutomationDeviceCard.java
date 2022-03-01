/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:14:57 PM
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;

public class AutomationDeviceCard {

    private int id;
    private AutomationDevice automationDevice;
    private Type type;
    private Status status;
    private String rfNo;
    private String name;
    private int resultId;

    public AutomationDeviceCard() {
        this.automationDevice = new AutomationDevice();
        this.type = new Type();
        this.status = new Status();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AutomationDevice getAutomationDevice() {
        return automationDevice;
    }

    public void setAutomationDevice(AutomationDevice automationDevice) {
        this.automationDevice = automationDevice;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRfNo() {
        return rfNo;
    }

    public void setRfNo(String rfNo) {
        this.rfNo = rfNo;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    @Override
    public String toString() {
        return String.valueOf(this.rfNo);
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}

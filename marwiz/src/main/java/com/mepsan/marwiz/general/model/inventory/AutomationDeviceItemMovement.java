/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.01.2020 09:54:28
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class AutomationDeviceItemMovement extends WotLogging {

    private int id;
    private AutomationDeviceItem automationDeviceItem;
    private BigDecimal quantity;
    private boolean isDirection;
    private int type;
    private Date processDate;

    public AutomationDeviceItemMovement() {
        this.automationDeviceItem = new AutomationDeviceItem();
    }

    public AutomationDeviceItem getAutomationDeviceItem() {
        return automationDeviceItem;
    }

    public void setAutomationDeviceItem(AutomationDeviceItem automationDeviceItem) {
        this.automationDeviceItem = automationDeviceItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    @Override
    public String toString() {
        return this.automationDeviceItem.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

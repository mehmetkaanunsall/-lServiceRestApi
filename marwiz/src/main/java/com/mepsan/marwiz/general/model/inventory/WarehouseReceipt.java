/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 04:58:20
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

public class WarehouseReceipt extends WotLogging {

    private int id;
    private Warehouse warehouse;
    private String receiptNumber;
    private boolean isDirection;
    private Type type;
    private Date processDate;

    private String jsonMovements;
    private boolean logSapİsSend;

    public WarehouseReceipt() {

        this.warehouse = new Warehouse();
        this.type = new Type();
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getJsonMovements() {
        return jsonMovements;
    }

    public void setJsonMovements(String jsonMovements) {
        this.jsonMovements = jsonMovements;
    }

    public boolean isLogSapİsSend() {
        return logSapİsSend;
    }

    public void setLogSapİsSend(boolean logSapİsSend) {
        this.logSapİsSend = logSapİsSend;
    }

    @Override
    public String toString() {
        return this.receiptNumber;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}

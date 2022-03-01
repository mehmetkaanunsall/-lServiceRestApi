/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 01:00:15
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class WarehouseMovement extends WotLogging {

    private int id;
    private Warehouse warehouse;
    private WarehouseReceipt warehouseReceipt;
    private boolean isDirection;
    private Stock stock;
    private BigDecimal quantity;
    private WasteItemInfo wasteItemInfo;
    private int type;
    private BigDecimal exitingWarehouseCurrentAmount;
    private BigDecimal entryingWarehouseCurrentAmount;

    public WarehouseMovement() {

        this.warehouse = new Warehouse();
        this.warehouseReceipt = new WarehouseReceipt();
        this.stock = new Stock();
        this.wasteItemInfo = new WasteItemInfo();
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

    public WarehouseReceipt getWarehouseReceipt() {
        return warehouseReceipt;
    }

    public void setWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        this.warehouseReceipt = warehouseReceipt;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public WasteItemInfo getWasteItemInfo() {
        return wasteItemInfo;
    }

    public void setWasteItemInfo(WasteItemInfo wasteItemInfo) {
        this.wasteItemInfo = wasteItemInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getExitingWarehouseCurrentAmount() {
        return exitingWarehouseCurrentAmount;
    }

    public void setExitingWarehouseCurrentAmount(BigDecimal exitingWarehouseCurrentAmount) {
        this.exitingWarehouseCurrentAmount = exitingWarehouseCurrentAmount;
    }

    public BigDecimal getEntryingWarehouseCurrentAmount() {
        return entryingWarehouseCurrentAmount;
    }

    public void setEntryingWarehouseCurrentAmount(BigDecimal entryingWarehouseCurrentAmount) {
        this.entryingWarehouseCurrentAmount = entryingWarehouseCurrentAmount;
    }

    

    @Override
    public String toString() {
        return this.stock.getName();
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }

}

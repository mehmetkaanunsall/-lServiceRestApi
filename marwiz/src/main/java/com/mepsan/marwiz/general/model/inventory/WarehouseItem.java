/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 01:29:42
 */

package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;


public class WarehouseItem extends WotLogging {

    private int id;
    private Warehouse wareHouse;
    private WarehouseReceipt warehouseReceipt;
    private Stock stock;
    private BigDecimal quantity;
    private BigDecimal minStockLevel;

    public WarehouseItem() {
        this.stock=new Stock();
        this.wareHouse=new Warehouse();
        this.warehouseReceipt=new WarehouseReceipt();
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Warehouse getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(Warehouse wareHouse) {
        this.wareHouse = wareHouse;
    }

    public WarehouseReceipt getWarehouseReceipt() {
        return warehouseReceipt;
    }

    public void setWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        this.warehouseReceipt = warehouseReceipt;
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

    public BigDecimal getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(BigDecimal minStockLevel) {
        this.minStockLevel = minStockLevel;
    }
    
    @Override
    public String toString() {
        return this.stock.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
    
    
}

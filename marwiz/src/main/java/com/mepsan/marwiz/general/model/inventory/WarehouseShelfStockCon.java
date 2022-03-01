/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 02:46:37
 */

package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;


public class WarehouseShelfStockCon extends WotLogging {

    private int id;
    private Stock stock;
    private WarehouseShelf warehouseShelf;

    public WarehouseShelfStockCon() {
        this.stock=new Stock();
        this.warehouseShelf=new WarehouseShelf();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public WarehouseShelf getWarehouseShelf() {
        return warehouseShelf;
    }

    public void setWarehouseShelf(WarehouseShelf warehouseShelf) {
        this.warehouseShelf = warehouseShelf;
    }
    

    @Override
    public int hashCode() {
        return this.getId();
    }
}

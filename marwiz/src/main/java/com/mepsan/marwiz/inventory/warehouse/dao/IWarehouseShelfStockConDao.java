/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 03:24:10
 */

package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IWarehouseShelfStockConDao extends ICrud<WarehouseShelfStockCon> {

    public List<WarehouseShelfStockCon> findAll(String where);
    
    public int delete(WarehouseShelfStockCon warehouseShelfStockCon);
}

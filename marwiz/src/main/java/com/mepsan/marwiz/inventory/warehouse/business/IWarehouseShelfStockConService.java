/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 04:20:58
 */

package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;


public interface IWarehouseShelfStockConService extends ICrudService<WarehouseShelfStockCon> {

     public List<WarehouseShelfStockCon> findAll(String where,WarehouseShelfStockCon warehouseShelfStockCon);
     
     public int delete(WarehouseShelfStockCon warehouseShelfStockCon);
}

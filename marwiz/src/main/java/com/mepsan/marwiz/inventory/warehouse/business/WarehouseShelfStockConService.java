/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 04:23:25
 */
package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseShelfStockConDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseShelfStockConService implements IWarehouseShelfStockConService {

    @Autowired
    private IWarehouseShelfStockConDao warehouseShelfStockConDao;

    public void setWarehouseShelfStockConDao(IWarehouseShelfStockConDao warehouseShelfStockConDao) {
        this.warehouseShelfStockConDao = warehouseShelfStockConDao;
    }
     
    
    @Override
    public List<WarehouseShelfStockCon> findAll(String where, WarehouseShelfStockCon warehouseShelfStockCon) {
        if (where.equals("stockTab")) {
            
            where = " and wssc.stock_id = " + warehouseShelfStockCon.getStock().getId() + " and ws.warehouse_id = " + warehouseShelfStockCon.getWarehouseShelf().getWareHouse().getId() + " ";
        }else if (where.equals("shelfTab")){
            
            where = " and wssc.warehouseshelf_id = " + warehouseShelfStockCon.getWarehouseShelf().getId() + " " ;

        }
        return warehouseShelfStockConDao.findAll(where);
    }

    @Override
    public int create(WarehouseShelfStockCon obj) {
        return warehouseShelfStockConDao.create(obj);
    }

    @Override
    public int update(WarehouseShelfStockCon obj) {
        return warehouseShelfStockConDao.update(obj);
    }

    @Override
    public int delete(WarehouseShelfStockCon warehouseShelfStockCon) {
       return warehouseShelfStockConDao.delete(warehouseShelfStockCon);
    }

}

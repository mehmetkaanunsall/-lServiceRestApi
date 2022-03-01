/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 12:32:22
 */

package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseShelfDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


public class WarehouseShelfService implements IWarehouseShelfService{

    @Autowired
    private IWarehouseShelfDao warehouseShelfDao;
    
    @Override
    public List<WarehouseShelf> findAll(Warehouse warehouse) {
        return warehouseShelfDao.findAll(warehouse);
    }

    @Override
    public int create(WarehouseShelf obj) {
        return warehouseShelfDao.create(obj);
    }

    @Override
    public int update(WarehouseShelf obj) {
        return warehouseShelfDao.update(obj);   
    }

    @Override
    public List<WarehouseShelf> selectShelfWithoutCon(Warehouse warehouse, Stock stock) {
        return warehouseShelfDao.selectShelfWithoutCon(warehouse, stock);
    }

    @Override
    public int delete(WarehouseShelf warehouseShelf) {
        return warehouseShelfDao.delete(warehouseShelf);
    }

}

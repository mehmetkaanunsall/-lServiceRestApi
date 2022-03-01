/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 02:50:05
 */
package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseService implements IWarehouseService {

    @Autowired
    private IWarehouseDao warehouseDao;

    public void setWareHouseDao(IWarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseDao.findAll();
    }

    @Override
    public int create(Warehouse obj) {
        return warehouseDao.create(obj);
    }

    @Override
    public int update(Warehouse obj) {
        return warehouseDao.update(obj);
    }

    @Override
    public List<Warehouse> selectListWarehouse(String where) {
        return warehouseDao.selectListWarehouse(where);
    }

    @Override
    public List<Warehouse> selectListWarehouse(Stock stock,String where, Branch branch) {
        return warehouseDao.selectListWarehouse(stock,where, branch);
    }

    @Override
    public List<Warehouse> selectListAllWarehouse(String where) {
        return warehouseDao.selectListAllWarehouse(where);
    }

    @Override
    public int delete(Warehouse warehouse) {
        return warehouseDao.delete(warehouse);
    }

    @Override
    public int testBeforeDelete(Warehouse warehouse) {
        return warehouseDao.testBeforeDelete(warehouse);
    }

    @Override
    public List<Warehouse> selectListWarehouseForBranch(Branch branch, String where) {
        return warehouseDao.selectListWarehouseForBranch(branch, where);
    }

}

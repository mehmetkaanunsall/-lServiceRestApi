package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseMovementTabDao;

/**
 *
 * @author Samet Dağ
 */
public class WarehouseMovementTabService implements IWarehouseMovementTabService {

    @Autowired
    public IWarehouseMovementTabDao warehouseMovementDao;

    @Override
    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, List<Stock> stock, Warehouse warehouse) {
        return warehouseMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, opType, begin, end, stock, warehouse);
    }

    @Override
    public int count(String where, List<Stock> stock, int opType, Date begin, Date end, Warehouse warehouse) {
        return warehouseMovementDao.count(where, stock, opType, begin, end, warehouse);
    }

}

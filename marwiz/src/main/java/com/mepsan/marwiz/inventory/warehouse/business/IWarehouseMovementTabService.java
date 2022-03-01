package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Samet Dağ
 */
public interface IWarehouseMovementTabService {

    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, List<Stock> stock, Warehouse warehouse);

    public int count(String where, List<Stock> stock, int opType, Date begin, Date end, Warehouse warehouse);
}

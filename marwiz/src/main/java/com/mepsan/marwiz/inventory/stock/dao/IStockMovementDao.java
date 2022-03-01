/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:40:48
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IStockMovementDao {

    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch);

    public StockMovement count(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch);

    public List<StockMovement> listOfWarehouseAvailability(Stock stock, Warehouse warehouse, List<Branch> listOfBranch);

    public String exportData(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, String branchList);

    public DataSource getDatasource();

}

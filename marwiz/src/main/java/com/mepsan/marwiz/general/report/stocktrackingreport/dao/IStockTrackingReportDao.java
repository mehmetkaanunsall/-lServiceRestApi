/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 04:01:57
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.dao;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IStockTrackingReportDao extends ILazyGrid<StockTrackingReport> {

    public String exportData(String where, String branchID);

    public DataSource getDatasource();

    public List<Warehouse> listWarehouse(String branchList);

    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);
}

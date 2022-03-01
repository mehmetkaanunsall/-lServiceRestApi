/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:22:37
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IMovementReportBetweenWarehouseTakingsDao {

    public List<MovementReportBetweenWarehouseTakings> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings);

    public List<MovementReportBetweenWarehouseTakings> totals(String where, MovementReportBetweenWarehouseTakings obj);
    
    public int count(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings);

    public List<StockTaking> listOfTaking(StockTaking stockTaking);

    public String exportData(String where);

    public DataSource getDatasource();

}

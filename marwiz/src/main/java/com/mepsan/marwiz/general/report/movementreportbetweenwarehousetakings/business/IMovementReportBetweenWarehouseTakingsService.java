/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:27:23
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.business;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao.MovementReportBetweenWarehouseTakings;
import java.util.List;
import java.util.Map;

public interface IMovementReportBetweenWarehouseTakingsService  {

    
    public List<MovementReportBetweenWarehouseTakings> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings);

    public List<MovementReportBetweenWarehouseTakings> totals(String where, MovementReportBetweenWarehouseTakings obj);
    
    public String createWhere(MovementReportBetweenWarehouseTakings obj);
    
    public int count(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings);
    
    public List<StockTaking> listOfTaking(StockTaking stockTaking);
    
    public void exportPdf(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale);

    public void exportExcel(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale);

    public String exportPrinter(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale);

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 01:49:03
 */
package com.mepsan.marwiz.general.report.fulltakingreport.business;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReport;
import java.util.List;
import java.util.Map;

public interface IFullTakingReportService {

    public List<FullTakingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FullTakingReport productInventoryReport);

    public int count(String where, FullTakingReport productInventoryReport);

    public String createWhere(FullTakingReport obj,int differentType);

    public void exportPdf(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList,int differentType, String totalPurchase,String totalSale);

    public void exportExcel(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList,int differentType, String totalPurchase,String totalSale);

    public String exportPrinter(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList,int differentType, String totalPurchase,String totalSale);

    public List<FullTakingReport> totals(String where, FullTakingReport fullTakingReport);
}

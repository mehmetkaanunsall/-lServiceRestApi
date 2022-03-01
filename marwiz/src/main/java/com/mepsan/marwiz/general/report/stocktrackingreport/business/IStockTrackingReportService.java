/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 04:03:33
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.stocktrackingreport.dao.StockTrackingReport;
import java.util.List;
import java.util.Map;

public interface IStockTrackingReportService extends IReportService<StockTrackingReport> {

    public void exportPdf(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList);

    public void exportExcel(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList);

    public String exportPrinter(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList);

    public List<Warehouse> listWarehouse(String branchList);

    public String createWhere(StockTrackingReport obj, List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier);

    public int count(String where, String branchList);

    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

}

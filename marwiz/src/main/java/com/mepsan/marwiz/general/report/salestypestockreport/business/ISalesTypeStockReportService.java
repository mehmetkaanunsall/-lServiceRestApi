/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:41:14
 */
package com.mepsan.marwiz.general.report.salestypestockreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.salestypestockreport.dao.SalesTypeStockReport;
import java.util.List;
import java.util.Map;

public interface ISalesTypeStockReportService extends IReportService<SalesTypeStockReport> {

    public List<SalesTypeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList);

    public List<SalesTypeStockReport> totals(String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList);

    public void exportPdf(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList);

    public void exportExcel(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList);

    public String exportPrinter(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList);

    public String createWhereForBranch(List<BranchSetting> listOfBranch);
}

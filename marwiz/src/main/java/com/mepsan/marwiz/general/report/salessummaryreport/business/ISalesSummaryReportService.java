/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:00:29
 */
package com.mepsan.marwiz.general.report.salessummaryreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.salessummaryreport.dao.SalesSummaryReport;
import java.util.List;
import java.util.Map;

public interface ISalesSummaryReportService extends IReportService<SalesSummaryReport> {

    public void exportPdf(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier);

    public void exportExcel(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier);

    public String exportPrinter(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier);

    public List<SalesSummaryReport> totals(String where, String branchList, SalesSummaryReport salesSummaryReport);

    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, SalesSummaryReport salesSummaryReport);

    public int count(String where, String branchList, SalesSummaryReport salesSummaryReport);

    public String createWhere(SalesSummaryReport obj, List<BranchSetting> branchList, boolean isCentralSupplier, int supplierType);

}

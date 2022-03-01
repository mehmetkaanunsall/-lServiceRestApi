/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:13:52
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.PurchaseSummaryReport;
import java.util.List;
import java.util.Map;

public interface IPurchaseSummaryReportService {

    public void exportPdf(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection);

    public void exportExcel(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier,List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection);

    public String exportPrinter(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier,List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection);

    public List<PurchaseSummaryReport> findAllDetail(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSummaryReport obj, String branchList);

    public int countDetail(String where, PurchaseSummaryReport obj, String branchList);

    public String createWhere(PurchaseSummaryReport obj, boolean isCentralSupplier, int supplierType);

    public List<PurchaseSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);

    public List<PurchaseSummaryReport> totals(String where, String branchList);

}

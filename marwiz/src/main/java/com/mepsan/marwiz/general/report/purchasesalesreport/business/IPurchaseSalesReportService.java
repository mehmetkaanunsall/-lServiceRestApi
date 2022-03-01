/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.10.2018 08:07:40
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.report.purchasesalesreport.dao.PurchaseSalesReport;
import java.util.List;
import java.util.Map;

public interface IPurchaseSalesReportService {

    public List<PurchaseSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList);

    public List<PurchaseSalesReport> count( PurchaseSalesReport obj, PurchaseSalesReport selectedObject);

    public List<PurchaseSalesReport> stockDetail(String where, PurchaseSalesReport obj, String branchList);

    public void exportPdf(String where, PurchaseSalesReport purchaseSalesReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, int centralIngetrationInf, List<PurchaseSalesReport> listOfPurchaseSaleReports, boolean isCentralSupplier);

    public void exportExcel(String where, PurchaseSalesReport purchaseSalesReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, int centralIngetrationInf, boolean isCentralSupplier);

    public String exportPrinter(String where, PurchaseSalesReport purchaseSalesReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, int centralIngetrationInf, boolean isCentralSupplier);

    public String createWhere(PurchaseSalesReport obj,List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier);

    public List<TaxGroup> listOfTaxGroup(int type, List<BranchSetting> branchList);
}

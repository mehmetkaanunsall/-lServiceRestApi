/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 12:09:28
 */
package com.mepsan.marwiz.general.report.salesdetailreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.salesdetailreport.dao.SalesDetailReport;
import java.util.List;
import java.util.Map;

public interface ISalesDetailReportService extends IReportService<SalesDetailReport> {

    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, SalesDetailReport salesDetailReport);

    public void exportPdf(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier ,String subTotalSalesQuantity , String subTotalMoney );

    public void exportExcel(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier , String subTotalSalesQuantity , String subTotalMoney );

    public String exportPrinter(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalSalesQuantity , String subTotalMoney );

    public int count(String where, String branchList, SalesDetailReport salesDetailReport);

    public String createWhere(SalesDetailReport obj, List<BranchSetting> branchList, boolean isCentralSupplier, int supplierType);
    
    public List<SalesDetailReport> totals(String where, String branchList, SalesDetailReport salesDetailReport);

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2020 04:13:10
 */
package com.mepsan.marwiz.general.report.freestockreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.freestockreport.dao.FreeStockReport;
import java.util.List;
import java.util.Map;

public interface IFreeStockReportService  {

    public void exportPdf(String where, FreeStockReport freeStockReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier);

    public void exportExcel(String where, FreeStockReport freeStockReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier);

    public String exportPrinter(String where, FreeStockReport freeStockReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier);
    
    public String createWhere(FreeStockReport obj, boolean isCentralSupplier, int supplierType);
    
    public List<FreeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);

}

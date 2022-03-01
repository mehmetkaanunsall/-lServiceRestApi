package com.mepsan.marwiz.general.report.purchasedetailreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.PurchaseDetailReport;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elif.mart
 */
public interface IPurchaseDetailReportService {

    public void exportPdf(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier ,String subTotalPurchaseQuantity , String subTotalMoney );

    public void exportExcel(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier ,String subTotalPurchaseQuantity , String subTotalMoney);

    public String exportPrinter(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier ,String subTotalPurchaseQuantity , String subTotalMoney);
    
    public String createWhere(PurchaseDetailReport obj, boolean isCentralSupplier, int supplierType);
    
    public List<PurchaseDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);
    
    public List<PurchaseDetailReport> totals(String where ,String branchList);

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 01:26:58
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.salesreceiptreport.dao.SalesReport;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ISalesReceiptReportService extends IReportService<SalesReport> {

    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Object param,String branchList, SalesReport salesReport);

    public int count(String where, Object param,String branchList, SalesReport salesReport);

    public String findFieldGroup(int subTotalValue);

    public void exportPdf(String where, SalesReport salesReport, List<Boolean> toogleList, List<SalesReport> listOfTotals,String branchList,List<BranchSetting> selectedBranchList);

    public void exportExcel(String where, SalesReport salesReport, List<Boolean> toogleList, List<SalesReport> listOfTotals,String branchList,List<BranchSetting> selectedBranchList);

    public String exportPrinter(String where, SalesReport salesReport, List<Boolean> toogleLis, List<SalesReport> listOfTotalst,String branchList,List<BranchSetting> selectedBranchList);

    public List<SaleItem> findSaleItem(SalesReport salesReport);

    public List<SalePayment> findSalePayment(SalesReport salesReport);
    
    public List<SalesReport> totals(String where,String branchList, SalesReport salesReport);
}

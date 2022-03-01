/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:40:13
 */
package com.mepsan.marwiz.general.report.salesreturnreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.ReceiptReturnReport;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ISalesReturnReportService extends IReportService<ReceiptReturnReport> {

    public int count(String where, String createWhereBranch);

    public void exportPdf(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals, Map<Integer, ReceiptReturnReport> currencyTotalsCollection);

    public void exportExcel(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals,  Map<Integer, ReceiptReturnReport> currencyTotalsCollection);

    public String exportPrinter(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals, Map<Integer, ReceiptReturnReport> currencyTotalsCollection);

    public String createWhereBranch(List<BranchSetting> listOfBranch);

    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch);

    public List<ReceiptReturnReport> totals(String where, String whereBranch);
}

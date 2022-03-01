/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 03:21:21
 */
package com.mepsan.marwiz.general.report.dailysalesreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.DailySalesReport;
import java.util.List;

public interface IDailySalesReportService {

    public DailySalesReport findAll(DailySalesReport dailySalesReport, String branchList, String sortBy);

    public void exportPdf(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate,int sorting, boolean  sortby);

    public void exportExcel(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate, int sorting, boolean  sortby);

    public String exportPrinter(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate,int sorting, boolean  sortby);

}

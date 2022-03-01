/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.03.2018 09:03:55
 */
package com.mepsan.marwiz.general.report.totalgiroreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.TotalGiroReport;
import java.util.List;

public interface ITotalGiroReportService extends IReportService<TotalGiroReport> {

    public void exportPdf(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList);

    public void exportExcel(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList);

    public String exportPrinter(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList);

    public List<TotalGiroReport> findAll(String where, String branchList);

    public String createWhereBranch(List<BranchSetting> listOfBranch);
}

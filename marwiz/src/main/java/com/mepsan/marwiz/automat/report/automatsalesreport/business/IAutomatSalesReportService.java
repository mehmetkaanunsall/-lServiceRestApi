/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 01:52:30
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.business;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import java.util.List;
import java.util.Map;

public interface IAutomatSalesReportService {

    public List<AutomatSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Object param);

    public List<AutomatSalesReport> totals(String where);

    public String createWhere(AutomatSalesReport obj);

    public String findFieldGroup(int subTotalValue);

    public void exportPdf(String where, AutomatSalesReport salesReport, List<Boolean> toogleList, List<AutomatSalesReport> listOfTotals);

    public void exportExcel(String where, AutomatSalesReport salesReport, List<Boolean> toogleList, List<AutomatSalesReport> listOfTotals);

    public String exportPrinter(String where, AutomatSalesReport salesReport, List<Boolean> toogleLis, List<AutomatSalesReport> listOfTotalst);

}

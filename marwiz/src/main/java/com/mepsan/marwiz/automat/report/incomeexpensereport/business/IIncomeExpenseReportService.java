/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:58:04 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.business;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import java.util.List;
import java.util.Map;

public interface IIncomeExpenseReportService {


    public int count(String where);

    public String createWhere(AutomatSalesReport obj);

    public String exportData(String where);

    public void exportPdf(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list);

    public void exportExcel(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list);

    public String exportPrinter(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list);

    public List<AutomatSalesReport> findAll(String where);

    public List<AutomatSalesReport> listOfSaleWaste(String where);

    public List<AutomatSalesReport> listOfIncomeExpense(String where);

    public List<AutomatSalesReport> listOfDetail(String where);

}

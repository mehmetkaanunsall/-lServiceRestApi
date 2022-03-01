/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:57:55 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.dao;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IIncomeExpenseReportDao {

    public int count(String where);

    public DataSource getDatasource();

    public String exportData(String where);

    public List<AutomatSalesReport> findAll(String where);

    public List<AutomatSalesReport> listOfSaleWaste(String where);

    public List<AutomatSalesReport> listOfIncomeExpense(String where);

    public List<AutomatSalesReport> listOfDetail(String where);

}

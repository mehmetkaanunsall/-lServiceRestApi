/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 05:29:32
 */
package com.mepsan.marwiz.general.report.profitmarginreport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IProfitMarginReportDao {

    public String exportData(ProfitMarginReport profitMarginReport, String where,String branchList,int centralIngetrationInf);

    public List<ProfitMarginReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);

    public DataSource getDatasource();

    public List<ProfitMarginReport> findAllCategory(ProfitMarginReport profitMarginReport, String where, String branchList, int centralIngetrationInf);

    public List<ProfitMarginReport> totals(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);

   // public List<ProfitMarginReport> totalsCategory(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);

}

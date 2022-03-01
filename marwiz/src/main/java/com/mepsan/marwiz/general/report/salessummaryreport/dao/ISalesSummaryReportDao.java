/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:04:15
 */
package com.mepsan.marwiz.general.report.salessummaryreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ISalesSummaryReportDao extends ILazyGrid<SalesSummaryReport> {

    public String exportData(String where, String branchList, SalesSummaryReport salesSummaryReport);

    public DataSource getDatasource();

    public List<SalesSummaryReport> totals(String where, String branchList, SalesSummaryReport salesSummaryReport);

    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, SalesSummaryReport salesSummaryReport);

    public int count(String where, String branchList, SalesSummaryReport salesSummaryReport);

}

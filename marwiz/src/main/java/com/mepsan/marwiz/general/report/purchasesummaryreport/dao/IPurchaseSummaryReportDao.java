/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:01:05
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.ReceiptReturnReport;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IPurchaseSummaryReportDao {

    public String exportData(String where, String branchList);

    public DataSource getDatasource();

    public List<PurchaseSummaryReport> findAllDetail(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSummaryReport obj, String branchList);

    public int countDetail(String where, PurchaseSummaryReport obj, String branchList);

    public List<PurchaseSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);

    public List<PurchaseSummaryReport> totals(String where, String branchList);

}

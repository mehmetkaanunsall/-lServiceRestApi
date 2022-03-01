/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:39:42
 */
package com.mepsan.marwiz.general.report.salesreturnreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ISalesReturnReportDao extends ILazyGrid<ReceiptReturnReport> {

    public String exportData(String where, String whereBranch);

    public DataSource getDatasource();

    public int count(String where, String whereBranch);

    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch);

    public List<ReceiptReturnReport> totals(String where, String whereBranch);
}

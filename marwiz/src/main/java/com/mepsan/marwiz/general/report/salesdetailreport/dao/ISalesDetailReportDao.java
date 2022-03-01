/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 12:07:40
 */
package com.mepsan.marwiz.general.report.salesdetailreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ISalesDetailReportDao extends ILazyGrid<SalesDetailReport> {

    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, SalesDetailReport salesDetailReport);

    public String exportData(String where, String branchList, SalesDetailReport salesDetailReport);

    public DataSource getDatasource();

    public int count(String where, String branchList, SalesDetailReport salesDetailReport);
    
    public List<SalesDetailReport> totals(String where, String branchList, SalesDetailReport salesDetailReport);

}

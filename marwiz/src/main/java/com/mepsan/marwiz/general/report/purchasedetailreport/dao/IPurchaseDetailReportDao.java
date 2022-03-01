package com.mepsan.marwiz.general.report.purchasedetailreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author elif.mart
 */
public interface IPurchaseDetailReportDao {

    public String exportData(String where, String branchList);

    public DataSource getDatasource();
    
    public List<PurchaseDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);
    
    public List<PurchaseDetailReport> totals (String where , String branchList);
}

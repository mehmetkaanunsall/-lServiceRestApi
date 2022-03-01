/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:40:49
 */
package com.mepsan.marwiz.general.report.salestypestockreport.dao;

import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ISalesTypeStockReportDao extends ICrud<SalesTypeStockReport> {
    
    public List<SalesTypeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, SalesTypeStockReport salesTypeStockReport,String whereBranchList);

    public List<SalesTypeStockReport> totals(String where,SalesTypeStockReport salesTypeStockReport,String whereBranchList);
    
    public String exportData(String where, SalesTypeStockReport salesTypeStockReport,String whereBranchList);
    
    public DataSource getDatasource();
}

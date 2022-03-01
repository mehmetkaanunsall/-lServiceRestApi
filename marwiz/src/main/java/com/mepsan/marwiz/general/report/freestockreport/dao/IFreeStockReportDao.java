/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2020 04:13:30
 */
package com.mepsan.marwiz.general.report.freestockreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IFreeStockReportDao {

    public String exportData(String where, String branchList);

    public DataSource getDatasource();
    
    public List<FreeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList);

    public int count(String where, String branchList);

}

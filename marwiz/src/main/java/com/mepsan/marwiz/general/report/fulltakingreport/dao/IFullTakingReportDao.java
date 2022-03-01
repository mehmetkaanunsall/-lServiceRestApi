/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 01:46:29
 */
package com.mepsan.marwiz.general.report.fulltakingreport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IFullTakingReportDao {

    public List<FullTakingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FullTakingReport productInventoryReport);

    public int count(String where, FullTakingReport productInventoryReport);

    public String exportData(String where);

    public DataSource getDatasource();

    public List<FullTakingReport> totals(String where, FullTakingReport fullTakingReport);
}

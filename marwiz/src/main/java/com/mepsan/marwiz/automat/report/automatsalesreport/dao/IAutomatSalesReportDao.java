/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 01:48:31
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IAutomatSalesReportDao {

    public List<AutomatSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String subTotal);

    public List<AutomatSalesReport> totals(String where);

    public String exportData(String where, String subTotal);

    public DataSource getDatasource();

}

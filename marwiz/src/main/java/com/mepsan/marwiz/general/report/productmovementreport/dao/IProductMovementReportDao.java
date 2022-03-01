/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 02:01:20
 */

package com.mepsan.marwiz.general.report.productmovementreport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;


public interface IProductMovementReportDao {

    public List<ProductMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where,ProductMovementReport obj);

    public int count(String where,ProductMovementReport obj);
    
    public String exportData(String where,ProductMovementReport obj);
    
    public DataSource getDatasource();
    
    public List<ProductMovementReport> totals(String where,ProductMovementReport obj);
}

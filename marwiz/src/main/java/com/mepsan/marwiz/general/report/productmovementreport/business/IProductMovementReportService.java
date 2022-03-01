/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 02:04:27
 */
package com.mepsan.marwiz.general.report.productmovementreport.business;

import com.mepsan.marwiz.general.report.productmovementreport.dao.ProductMovementReport;
import java.util.List;
import java.util.Map;

public interface IProductMovementReportService {

    public List<ProductMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductMovementReport obj);

    public int count(String where, ProductMovementReport obj);

    public void exportPdf(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier);

    public void exportExcel(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier);

    public String exportPrinter(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier);

    public String createWhere(ProductMovementReport obj, boolean isCentralIntegration, int supplierType);
    
    public List<ProductMovementReport> totals(String where,ProductMovementReport obj);

}

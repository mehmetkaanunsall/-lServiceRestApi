/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IStockInventoryReportDao {
    
    public List<StockInventoryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockInventoryReport obj, String branchList,int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier);

    public List<StockInventoryReport> totals(String where, StockInventoryReport obj, String branchList ,int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier);

    public String exportData(String where, StockInventoryReport obj,String branchList ,int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier);

    public DataSource getDatasource();
}

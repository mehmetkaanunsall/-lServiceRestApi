/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import java.util.List;
import javax.sql.DataSource;
import java.util.Map;

/**
 *
 * @author ebubekir.buker
 */
public interface IFuelSalesReportDao{
    
    public List<FuelSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, FuelSalesReport fuelSalesReportsalesReport);

    public int count(String where);

    public String exportData(String where, String branchList,FuelSalesReport fuelSalesReport);

    public DataSource getDatasource();

}

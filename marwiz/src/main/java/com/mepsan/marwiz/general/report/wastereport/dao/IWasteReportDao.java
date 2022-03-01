/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IWasteReportDao {
   
    public List<WasteReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where,WasteReport obj, String branchList);

    public List<WasteReport> totals(String where, String branchList);
    
    public String exportData(String where,WasteReport obj, String branchList);
    
    public DataSource getDatasource();
}

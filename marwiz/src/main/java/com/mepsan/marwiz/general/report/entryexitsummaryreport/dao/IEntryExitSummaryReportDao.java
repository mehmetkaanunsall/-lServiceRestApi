/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IEntryExitSummaryReportDao {
    
    public List<EntryExitSummary> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where,EntryExitSummary obj);

    public int count(String where,EntryExitSummary obj);
    
    public String exportData(String where,EntryExitSummary obj);
    
    public DataSource getDatasource();
}

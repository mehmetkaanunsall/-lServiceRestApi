/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.business;

import com.mepsan.marwiz.general.report.entryexitsummaryreport.dao.EntryExitSummary;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IEntryExitSummaryReportService {

    public List<EntryExitSummary> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, EntryExitSummary obj);

    public int count(String where, EntryExitSummary obj);

    public void exportPdf(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier);

    public void exportExcel(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier);

    public String exportPrinter(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier);

    public String createWhere(EntryExitSummary obj, boolean  isCentralIntegration, int supplierType);
}

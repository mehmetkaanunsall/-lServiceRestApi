/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import com.mepsan.marwiz.general.report.wastereport.dao.WasteReport;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IWasteReportService{
    
    public String createWhere(WasteReport obj);
     
    public List<WasteReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WasteReport obj, String branchList);

    public List<WasteReport> totals(String where, String branchList);

    public void exportPdf(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals);

    public void exportExcel(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals);

    public String exportPrinter(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals);
   
}

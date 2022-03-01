/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.report.stockinventoryreport.dao.StockInventoryReport;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IStockInventoryReportService {

    public String createWhere(StockInventoryReport obj, int centralIntegrationIf);

    public List<StockInventoryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockInventoryReport obj, String whereBranch, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier);

    public List<StockInventoryReport> totals(String where, StockInventoryReport obj, String whereBranch, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier);

    public void exportPdf(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType);

    public void exportExcel(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType);

    public String exportPrinter(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType);

}

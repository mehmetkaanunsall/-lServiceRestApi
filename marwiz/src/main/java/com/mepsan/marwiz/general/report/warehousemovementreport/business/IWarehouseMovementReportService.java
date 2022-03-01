/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.business;

import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.warehousemovementreport.dao.WarehouseMovementReport;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IWarehouseMovementReportService extends IReportService<WarehouseMovementReport> {

    public void exportPdf(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection);

    public void exportExcel(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection);

    public String exportPrinter(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection);

    public List<WarehouseMovementReport> totals(String where, WarehouseMovementReport obj);

    public List<WarehouseMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WarehouseMovementReport obj);

    public String createWhere(WarehouseMovementReport obj, int supplierType, boolean isCentralSupplier);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IWarehouseMovementReportDao extends ILazyGrid<WarehouseMovementReport> {

    public String exportData(String where, WarehouseMovementReport obj);

    public DataSource getDatasource();

    public List<WarehouseMovementReport> totals(String where, WarehouseMovementReport obj);

    public List<WarehouseMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WarehouseMovementReport obj);

}

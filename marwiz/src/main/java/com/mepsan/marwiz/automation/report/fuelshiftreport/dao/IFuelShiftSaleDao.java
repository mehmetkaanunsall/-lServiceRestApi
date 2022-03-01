/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author samet.dag
 */
public interface IFuelShiftSaleDao {

    public List<FuelShiftSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FuelShift fuelShift);

    public int count(String where, FuelShift fuelShift);

    public String exportData(String where, FuelShift fuelShift);

    public DataSource getDatasource();

    public List<FuelShiftSales> listPrintRecords(FuelShift fuelShift);

    public List<FuelShiftSales> totals(String where, FuelShift fuelShift);

}

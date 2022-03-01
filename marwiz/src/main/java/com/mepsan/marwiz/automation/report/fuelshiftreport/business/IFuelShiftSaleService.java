/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.business;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author samet.dag
 */
public interface IFuelShiftSaleService {

    public List<FuelShiftSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FuelShift fuelShift);

    public int count(String where, FuelShift fuelShift);

    public void exportPdf(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals);

    public void exportExcel(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals);

    public String exportPrinter(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals);

    public List<FuelShiftSales> listPrintRecords(FuelShift fuelShift);

    public List<FuelShiftSales> totals(String where, FuelShift fuelShift);

}

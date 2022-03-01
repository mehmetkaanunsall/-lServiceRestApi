/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.business;

import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ebubekir.buker
 */
public interface IFuelSalesReportService {

    public List<FuelSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, FuelSalesReport fuelSalesReport);

    public int count(String where);

    public void exportPdf(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, String branchList, List<Branch> selectedBranchList);

    public void exportExcel(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, List<FuelSalesReport> listOfTotals, String branchList, List<Branch> selectedBranchList);

    public String exportPrinter(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, List<FuelSalesReport> listOfTotalst, String branchList, List<Branch> selectedBranchList);

    public String createWhere(FuelSalesReport obj);
}

/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:33:22
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.business;

import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.FuelShiftReport;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IFuelShiftService {

    public List<FuelShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BranchSetting branchSetting);

    public String createWhere(FuelShiftReport obj);

    public List<FuelShiftReport> totals(String where, BranchSetting branchSetting);     

    public int createShift(FuelShift fuelShift);

    public List<FuelShiftSales> importFuelShiftFromTxt(InputStream inputStream, String shiftNo);
    
    public List<FuelShiftSales> importFuelShiftFromAsis(InputStream inputStream, String shiftNo);

    public List<FuelShiftSales> importFuelShiftFromXml(InputStream inputStream, String shiftNo, BranchSetting branchSetting);
    
    public List<FuelShiftSales> importFuelShiftFromTxtForStawiz(InputStream inputStream, String shiftNo);

    public FuelShift insertShiftAndShiftSales(List<FuelShiftSales> shiftSales);

    public List<FuelShiftSales> findAttendantSales(FuelShift fuelShift, BranchSetting branchSetting);

    public List<FuelShiftSales> findStockNameSales(FuelShift fuelShift, BranchSetting branchSetting);

    public List<FuelShiftSales> findSaleTypeSales(FuelShift fuelShift, BranchSetting branchSetting);

    public void exportPdf(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal);

    public void exportExcel(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal);

    public String exportPrinter(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal);

    public String jsonArrayIntegrationName(List<FuelShiftSales> listOfSaleStock, int processType);
}

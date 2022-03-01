/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:31:49
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IFuelShiftDao {

    public List<FuelShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BranchSetting branchSetting);

    public List<FuelShiftReport> totals(String where, BranchSetting branchSetting);

    public int createShift(FuelShift fuelShift);

    public FuelShift insertShiftAndShiftSales(String json);

    public List<FuelShiftSales> findAttendantSales(FuelShift fuelShift, BranchSetting branchSetting);

    public List<FuelShiftSales> findStockNameSales(FuelShift fuelShift, BranchSetting branchSetting);

    public List<FuelShiftSales> findSaleTypeSales(FuelShift fuelShift, BranchSetting branchSetting);

    public String exportData(String where, BranchSetting branchSetting);

    public DataSource getDatasource();
    
    public String findIntegrationName(String json, int processType);

}

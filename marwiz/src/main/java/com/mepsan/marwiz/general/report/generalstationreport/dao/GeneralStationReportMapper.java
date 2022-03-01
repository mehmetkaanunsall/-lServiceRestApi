/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author m.duzoylum
 */
public class GeneralStationReportMapper implements RowMapper<GeneralStation> {
    
    @Override
    public GeneralStation mapRow(ResultSet rs, int i) throws SQLException {
        
        GeneralStation generalStation = new GeneralStation();
        
        try {
            generalStation.getStock().getUnit().setUnitRounding(rs.getInt("unitrounding"));
        } catch (Exception e) {
        }
        
        try {
            
            generalStation.setId(rs.getInt("rownum"));
        } catch (Exception e) {
        }
        try {
            generalStation.setCount(rs.getInt("idcount"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getStock().setId(rs.getInt("stockid"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getBranchSetting().getBranch().setName(rs.getString("branchname"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getStock().getUnit().setSortName(rs.getString("unitsortname"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getSaleCurrencyId().setId(rs.getInt("salecurrencyid"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getPurchaseCurrencyid().setId(rs.getInt("purchasecurrencyid"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.getStock().setCode(rs.getString("stockcode"));
            generalStation.getStock().setBarcode(rs.getString("stockbarcode"));
            generalStation.getStock().setCenterProductCode(rs.getString("stockcentercode"));
            
        } catch (Exception e) {
        }
        
        try {
            generalStation.getStock().setName(rs.getString("stockname"));
        } catch (Exception e) {
        }
        try {
            generalStation.setAutomatName(rs.getString("automatname"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setRateofProfit(rs.getBigDecimal("rateofprofit"));
            generalStation.setProfitAmount(rs.getBigDecimal("profitamount"));
            generalStation.setProfitMargin(rs.getBigDecimal("profitmargin"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setRemainingQuantity(rs.getBigDecimal("remainingquantity"));
            generalStation.setRemainingAmount(rs.getBigDecimal("remainingamount"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setTransferAmount(rs.getBigDecimal("transferamount"));
            generalStation.setTransferQuantity(rs.getBigDecimal("transferquantity"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setPurchaseQuantity(rs.getBigDecimal("purchasequantity"));
            generalStation.setPurchaseAmount(rs.getBigDecimal("purchaseamount"));
            
            generalStation.setSalesAmount(rs.getBigDecimal("salesamount"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setSalesQuantity(rs.getBigDecimal("salesquantity"));
        } catch (Exception e) {
        }
        
        try {
            
            generalStation.getCategorization().setId(rs.getInt("categorizationid"));
            generalStation.getCategorization().setName(rs.getString("categorizationname"));
            
        } catch (Exception e) {
        }
        
        try {
            
            generalStation.getBranchSetting().getBranch().setId(rs.getInt("branchid"));
            
        } catch (Exception e) {
            
        }
        
        try {
            generalStation.getCategorization().setParentId(new Categorization());
            generalStation.getCategorization().getParentId().setId(rs.getInt("parentcategoryid"));
            generalStation.getCategorization().getParentId().setName(rs.getString("parentcategoryname"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setCost(rs.getBigDecimal("purchasecost"));
        } catch (Exception e) {
        }
        try {
            generalStation.getVendingMachine().setId(rs.getInt("vendingmachineid"));
            generalStation.getVendingMachine().setName(rs.getString("vendingmachinename"));
            generalStation.getVendingMachine().getDeviceType().setId(rs.getInt("vmtype_id"));
            generalStation.getVendingMachine().getDeviceType().setTag(rs.getString("vmtypename"));
            generalStation.getVendingMachine().getWarehouse().setId(rs.getInt("warehouse_id"));
        } catch (Exception e) {
        }
        
        try {
            generalStation.setElectricQuantity(rs.getBigDecimal("electricquantitiy"));
            generalStation.setElectricOperationTime(rs.getBigDecimal("slelectricoperationtime"));
            generalStation.setElectricExpense(rs.getBigDecimal("electricexpense"));
            generalStation.setTotalElectricAmount(rs.getBigDecimal("elecquantity"));
            generalStation.setWaterWorkingAmount(rs.getBigDecimal("waterworkingamount"));
            generalStation.setWaterWorkingTime(rs.getInt("waterworkingtime"));
            generalStation.setWaterExpense(rs.getBigDecimal("waterexpense"));
            generalStation.setWaterWaste(rs.getBigDecimal("waterwase"));
            generalStation.setWaste(rs.getBigDecimal("waste"));
            generalStation.setTotalIncome(rs.getBigDecimal("income"));
            generalStation.setTotalExpense(rs.getBigDecimal("expense"));
            generalStation.setTotalWinnings(rs.getBigDecimal("winngins"));
            
        } catch (Exception e) {
        }
        return generalStation;
    }
    
}

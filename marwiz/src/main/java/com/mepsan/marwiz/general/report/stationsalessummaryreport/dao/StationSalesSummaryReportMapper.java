/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:57:05 AM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StationSalesSummaryReportMapper implements RowMapper<StationSalesSummaryReport> {
    
    @Override
    public StationSalesSummaryReport mapRow(ResultSet rs, int i) throws SQLException {
        StationSalesSummaryReport salesSummaryReport = new StationSalesSummaryReport();
        
        try {// Akaryakıt Satışları

            salesSummaryReport.setFuelStockName(rs.getString("stckname"));
            salesSummaryReport.setFuelStockQuantity(rs.getBigDecimal("sfsliter"));
            salesSummaryReport.setFuelStockSalesTotal(rs.getBigDecimal("sfstotalmoney"));
            salesSummaryReport.setFuelStockUnitPrice(BigDecimal.ZERO);
            salesSummaryReport.getStockUnit().setId(rs.getInt("stckunitid"));
            salesSummaryReport.getStockUnit().setSortName(rs.getString("untsortname"));
        } catch (Exception e) {
        }
        
        try { // Akaryakıt Tahsilatları
            salesSummaryReport.setFuelSaleTypeId(rs.getInt("saletype_id"));
            salesSummaryReport.setFuelCollectionName(rs.getString("fstypename"));
            
        } catch (Exception e) {
        }
        
        try { // Market Satışları
            salesSummaryReport.setMarketSalesQuantity(rs.getBigDecimal("salequantity"));
            salesSummaryReport.setMarketSaleTotalMoney(rs.getBigDecimal("saletotal"));
            
        } catch (Exception e) {
        }
        
        try { // Market Tahsilatları

            salesSummaryReport.setMarketCollectionTypeName(rs.getString("typdname"));
            
        } catch (Exception e) {
        }
        
        try {
            salesSummaryReport.getBranchSetting().getBranch().setId(rs.getInt("brnid"));
            salesSummaryReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
            
            try {
                
                salesSummaryReport.getCurrency().setId(rs.getInt("shpcurrency_id"));
            } catch (Exception e) {
            }
            
            try {
                salesSummaryReport.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brncurrency_id"));
            } catch (Exception e) {
            }
            
            try {
                salesSummaryReport.getCurrency().setId(rs.getInt("slcurrency_id"));
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        try {
            
            salesSummaryReport.setMoreMoney(rs.getBigDecimal("acikfazla"));
            salesSummaryReport.setIncomeExpenseMoney(rs.getBigDecimal("gelirgider"));
            salesSummaryReport.setAccountCollectionPaymentMoney(rs.getBigDecimal("caritahsilatödeme"));
            
        } catch (Exception e) {
        }
        
        try {
            salesSummaryReport.setMarketCollectionTotalMoney(rs.getBigDecimal("marketsalecollections"));
            salesSummaryReport.setMarketDeptMoney(rs.getBigDecimal("marketacikfazla"));
        } catch (Exception e) {
        }
        
        try {
            salesSummaryReport.setFuelCollectionSalesTotal(rs.getBigDecimal("collectionstotal"));
        } catch (Exception e) {
        }
        
        return salesSummaryReport;
        
    }
    
}

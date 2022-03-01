/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:00:00 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatShiftReportPrintDetailMapper implements RowMapper<AutomatShiftReport> {
    
    @Override
    public AutomatShiftReport mapRow(ResultSet rs, int i) throws SQLException {
        
        AutomatShiftReport automatShiftReport = new AutomatShiftReport();
        try { // ödeme tipleri için 

            automatShiftReport.getAutomatSalesItem().setPaymentType(rs.getInt("slpaymenttype_id"));
            automatShiftReport.getAutomatSalesItem().getStock().setId(rs.getInt("slstockid"));
            automatShiftReport.getAutomatSalesItem().getStock().setName(rs.getString("stckname"));
            automatShiftReport.getAutomatSalesItem().setOperationAmount(rs.getBigDecimal("slliter"));
            automatShiftReport.getAutomatSalesItem().setTotalMoney(rs.getBigDecimal("sltotalmoney"));
            
        } catch (Exception e) {
        }
        
        try { // Platform Satışları için 

            automatShiftReport.getAutomatSalesItem().getPlatform().setId(rs.getInt("pltid"));
            automatShiftReport.getAutomatSalesItem().getPlatform().setPlatformNo(rs.getString("pltplatformno"));
            automatShiftReport.getAutomatSalesItem().getStock().setId(rs.getInt("slstock_id"));
            automatShiftReport.getAutomatSalesItem().getStock().setName(rs.getString("stckname"));
            automatShiftReport.getAutomatSalesItem().setOperationAmount(rs.getBigDecimal("slliter"));
            automatShiftReport.getAutomatSalesItem().setTotalMoney(rs.getBigDecimal("sltotalmoney"));
            
        } catch (Exception e) {
        }
        
        try {
            automatShiftReport.getAutomatSalesItem().getStock().setId(rs.getInt("slstockid"));
            automatShiftReport.getAutomatSalesItem().getStock().setName(rs.getString("stckname"));
            automatShiftReport.getAutomatSalesItem().setUnitPrice(rs.getBigDecimal("slunitprice"));
            automatShiftReport.getAutomatSalesItem().setOperationAmount(rs.getBigDecimal("slliter"));
            automatShiftReport.getAutomatSalesItem().setTotalMoney(rs.getBigDecimal("sltotalmoney"));
            automatShiftReport.getAutomatSalesItem().getCurrency().setId(rs.getInt("slcurrency_id"));
            
        } catch (Exception e) {
        }
        
        automatShiftReport.getAutomatSalesItem().getStock().getUnit().setId(rs.getInt("sliunit_id"));
        automatShiftReport.getAutomatSalesItem().getStock().getUnit().setSortName(rs.getString("untsortname"));
        automatShiftReport.getAutomatSalesItem().getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
        automatShiftReport.setTotalCount(rs.getInt("slidcount"));
        
        try {
            automatShiftReport.getAutomatSalesItem().getWashingMachine().setName(rs.getString("wshname"));
        } catch (Exception e) {
        }
        return automatShiftReport;
    }
    
}

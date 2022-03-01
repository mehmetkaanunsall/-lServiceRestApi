/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.02.2018 10:25:52
 */
package com.mepsan.marwiz.general.report.salessummaryreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesSummaryReportMapper implements RowMapper<SalesSummaryReport> {

    @Override
    public SalesSummaryReport mapRow(ResultSet rs, int i) throws SQLException {
        SalesSummaryReport salesSummaryReport = new SalesSummaryReport();

    
        
        salesSummaryReport.getStock().setId(rs.getInt("slistockid"));
        salesSummaryReport.setTotalGiroByStock(rs.getBigDecimal("totalgirobystock"));
        salesSummaryReport.getCurrency().setId(rs.getInt("slcurrency_id"));
        try {

            salesSummaryReport.getStock().setName(rs.getString("stckname"));
            salesSummaryReport.getStock().setBarcode(rs.getString("stckbarcode"));
            salesSummaryReport.getStock().setCode(rs.getString("stckcode"));
            salesSummaryReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            salesSummaryReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            salesSummaryReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            salesSummaryReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            salesSummaryReport.setUnitPrice(rs.getBigDecimal("sliunitprice"));
            salesSummaryReport.setCountQuantity(rs.getBigDecimal("countQuantity"));
            salesSummaryReport.setTotalCountByStock(rs.getBigDecimal("totalcountbystock"));

            salesSummaryReport.setCategory(rs.getString("category"));
            salesSummaryReport.setCategory(StaticMethods.findCategories(salesSummaryReport.getCategory()));
            salesSummaryReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            salesSummaryReport.getStock().getBrand().setName(rs.getString("brname"));
            salesSummaryReport.getStock().getSupplier().setId(rs.getInt("supplier_id"));
            salesSummaryReport.getStock().getSupplier().setName(rs.getString("accname"));
            salesSummaryReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            salesSummaryReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            salesSummaryReport.setTotalGiro(rs.getBigDecimal("giro"));

        } catch (Exception e) {
        }

        try {
            salesSummaryReport.getBranchSetting().getBranch().setId(rs.getInt("brnid"));
            salesSummaryReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return salesSummaryReport;
    }

}

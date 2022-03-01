/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class WasteReportMapper implements RowMapper<WasteReport> {

    @Override
    public WasteReport mapRow(ResultSet rs, int i) throws SQLException {
        WasteReport wasteReport = new WasteReport();

        wasteReport.setId(rs.getInt("whmid"));
        wasteReport.getStock().getUnit().setName(rs.getString("guntname"));
        wasteReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        wasteReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        wasteReport.setQuantity(rs.getBigDecimal("wsiquantity"));
        wasteReport.getCurrency().setId(rs.getInt("currency_id"));

        try {
            wasteReport.getWarehouseReceipt().setProcessDate(rs.getTimestamp("iwrprocessdate"));
            wasteReport.getStock().setId(rs.getInt("whmstock_id"));
            wasteReport.getStock().setName(rs.getString("stckname"));
            wasteReport.getStock().setBarcode(rs.getString("stckbarcode"));
            wasteReport.getStock().setCode(rs.getString("stckcode"));
            wasteReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            wasteReport.setCategory(rs.getString("category"));
            wasteReport.setCategory(StaticMethods.findCategories(wasteReport.getCategory()));
            wasteReport.setPurchasePrice(rs.getBigDecimal("unitprice"));
            wasteReport.setWasteCause(rs.getString("wsidescription"));
            wasteReport.setExpirationDate(rs.getTimestamp("wsiexpirationdate"));
        } catch (Exception e) {
        }
        try {
            wasteReport.setTaxRate(rs.getBigDecimal("taxrate"));
            wasteReport.getWasteReason().setName(rs.getString("wrname"));
            wasteReport.getWasteReason().setId(rs.getInt("wrid"));

        } catch (Exception e) {
        }
        try {
            wasteReport.setTotal(rs.getBigDecimal("total"));
        } catch (Exception e) {
        }
        
        try {
            wasteReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return wasteReport;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class StockInventoryReportMapper implements RowMapper<StockInventoryReport> {

    @Override
    public StockInventoryReport mapRow(ResultSet rs, int i) throws SQLException {
        StockInventoryReport stockInventoryReport = new StockInventoryReport();

        try {
            stockInventoryReport.getStock().setId(rs.getInt("stckid"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getStock().setName(rs.getString("stckname"));
            stockInventoryReport.getStock().setCode(rs.getString("stckcode"));
            stockInventoryReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            stockInventoryReport.getStock().setBarcode(rs.getString("stckbarcode"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.setLastSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            stockInventoryReport.setLasPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.setQuantity(rs.getBigDecimal("quantity"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getLastSaleCurreny().setId(rs.getInt("sicurrentsalecurrency_id"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getLastPurchaseCurreny().setId(rs.getInt("sicurrentpurchasecurrency_id"));
        } catch (Exception e) {

        }
        try {
            stockInventoryReport.setLastSaleCost(rs.getBigDecimal("salecost"));

        } catch (Exception e) {

        }
        try {
            stockInventoryReport.setLastPurchaseCost(rs.getBigDecimal("purchasecost"));

        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getTax().setId(rs.getInt("tgid"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getTax().setRate(rs.getBigDecimal("tgrate"));
        } catch (Exception e) {

        }

        try {
            stockInventoryReport.getStock().getUnit().setId(rs.getInt("guntid"));
            stockInventoryReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            stockInventoryReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        } catch (Exception e) {

        }
        try {
            stockInventoryReport.setCategory(rs.getString("category"));
            stockInventoryReport.setCategory(StaticMethods.findCategories(stockInventoryReport.getCategory()));
            stockInventoryReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            stockInventoryReport.getStock().getBrand().setName(rs.getString("brname"));
            stockInventoryReport.getStock().getSupplier().setId(rs.getInt("supplier_id"));
            stockInventoryReport.getStock().getSupplier().setName(rs.getString("accname"));
            stockInventoryReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            stockInventoryReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        } catch (Exception e) {
        }

        try {
            stockInventoryReport.getBranchSetting().getBranch().setId(rs.getInt("brnid"));
            stockInventoryReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));

        } catch (Exception e) {
        }
        return stockInventoryReport;
    }

}

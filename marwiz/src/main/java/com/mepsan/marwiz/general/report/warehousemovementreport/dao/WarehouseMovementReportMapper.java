/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.jdbc.core.RowMapper;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author esra.cabuk
 */
public class WarehouseMovementReportMapper implements RowMapper<WarehouseMovementReport> {
    
    @Override
    public WarehouseMovementReport mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseMovementReport warehouseMovementReport = new WarehouseMovementReport();
        warehouseMovementReport.setId(rs.getInt("iwmid"));
        try {
            warehouseMovementReport.setProcessDate(rs.getTimestamp("iwrprocessdate"));
            if (rs.getBoolean("iwmis_direction")) {
                warehouseMovementReport.setIsDirection(1);
            } else {
                warehouseMovementReport.setIsDirection(2);
            }
            warehouseMovementReport.getWarehouse().setId(rs.getInt("iwmwarehouse_id"));
            warehouseMovementReport.getWarehouse().setName(rs.getString("iwname"));
            warehouseMovementReport.getStock().setId(rs.getInt("iwmstock_id"));
            warehouseMovementReport.getStock().setName(rs.getString("stckname"));
            warehouseMovementReport.getStock().setBarcode(rs.getString("stckbarcode"));
            warehouseMovementReport.getStock().setCode(rs.getString("stckcode"));
            warehouseMovementReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            warehouseMovementReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            warehouseMovementReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            warehouseMovementReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            
            warehouseMovementReport.setCategory(rs.getString("category"));
            warehouseMovementReport.setCategory(StaticMethods.findCategories(warehouseMovementReport.getCategory()));
            warehouseMovementReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            warehouseMovementReport.getStock().getBrand().setName(rs.getString("brname"));
            warehouseMovementReport.getStock().getSupplier().setId(rs.getInt("supplier_id"));
            warehouseMovementReport.getStock().getSupplier().setName(rs.getString("accname"));
            warehouseMovementReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            warehouseMovementReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        } catch (Exception e) {
        }
        warehouseMovementReport.setQuantity(rs.getBigDecimal("iwmquantity"));
        warehouseMovementReport.setUnitPrice(rs.getBigDecimal("price"));
        warehouseMovementReport.setTotalTax(rs.getBigDecimal("totaltax") != null ? rs.getBigDecimal("totaltax") : BigDecimal.ZERO);
        warehouseMovementReport.setTotalMoney(rs.getBigDecimal("totalmoney") != null ? rs.getBigDecimal("totalmoney") : BigDecimal.ZERO);
        warehouseMovementReport.getCurrency().setId(rs.getInt("currency_id"));
        
        return warehouseMovementReport;
    }
    
}

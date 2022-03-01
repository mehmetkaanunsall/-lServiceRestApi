/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:49:17
 */
package com.mepsan.marwiz.general.report.salestypestockreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesTypeStockReportMapper implements RowMapper<SalesTypeStockReport> {

    @Override
    public SalesTypeStockReport mapRow(ResultSet rs, int i) throws SQLException {
        SalesTypeStockReport salesTypeStockReport = new SalesTypeStockReport();

        salesTypeStockReport.getStock().setId(rs.getInt("slistock_id"));
        salesTypeStockReport.setTotalMoney(rs.getBigDecimal("totalmoney"));
        salesTypeStockReport.getCurrency().setId(rs.getInt("slcurrency_id"));

        try {
            salesTypeStockReport.getStock().setName(rs.getString("stckname"));
            salesTypeStockReport.getStock().setCode(rs.getString("stckcode"));
            salesTypeStockReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            salesTypeStockReport.getStock().setBarcode(rs.getString("stckbarcode"));
            salesTypeStockReport.getUnit().setSortName(rs.getString("guntsortname"));
            salesTypeStockReport.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            salesTypeStockReport.setTotalQuantity(rs.getBigDecimal("totalquantity"));
        } catch (Exception e) {
        }

        try {
            salesTypeStockReport.getBranch().setId(rs.getInt("brnid"));
            salesTypeStockReport.getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return salesTypeStockReport;
    }
}

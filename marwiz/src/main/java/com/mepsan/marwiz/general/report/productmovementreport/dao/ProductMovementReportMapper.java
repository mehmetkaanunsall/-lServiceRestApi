/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 02:15:58
 */
package com.mepsan.marwiz.general.report.productmovementreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductMovementReportMapper implements RowMapper<ProductMovementReport> {

    @Override
    public ProductMovementReport mapRow(ResultSet rs, int i) throws SQLException {
        ProductMovementReport productMovementReport = new ProductMovementReport();
        productMovementReport.getStock().setId(rs.getInt("stckid"));
        productMovementReport.getCurrency().setId(rs.getInt("currency_id"));
        productMovementReport.setSalesPrice(rs.getBigDecimal("totalmoney"));

        try {
            productMovementReport.getStock().setName(rs.getString("stckname"));
            productMovementReport.getStock().setCode(rs.getString("stckcode"));
            productMovementReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            productMovementReport.getStock().setBarcode(rs.getString("stckbarcode"));
            productMovementReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            productMovementReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            productMovementReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            productMovementReport.setQuantity(rs.getBigDecimal("quantity"));
            productMovementReport.setUnitPrice(rs.getBigDecimal("unitprice"));
            productMovementReport.setCategory(rs.getString("category"));
            productMovementReport.setCategory(StaticMethods.findCategories(productMovementReport.getCategory()));
            productMovementReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            productMovementReport.getStock().getBrand().setName(rs.getString("brname"));
            productMovementReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
            productMovementReport.getStock().getSupplier().setName(rs.getString("accname"));
            productMovementReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            productMovementReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            productMovementReport.getBranch().setId(rs.getInt("branch_id"));
            productMovementReport.getBranch().setName(rs.getString("brcname"));

        } catch (Exception e) {

        }
        return productMovementReport;
    }

}

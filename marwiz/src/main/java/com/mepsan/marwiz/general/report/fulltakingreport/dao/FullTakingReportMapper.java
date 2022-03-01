/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 02:13:16
 */
package com.mepsan.marwiz.general.report.fulltakingreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FullTakingReportMapper implements RowMapper<FullTakingReport> {

    @Override
    public FullTakingReport mapRow(ResultSet rs, int i) throws SQLException {
        FullTakingReport productInventoryReport = new FullTakingReport();
        productInventoryReport.getStock().setId(rs.getInt("iwistock_id"));
        productInventoryReport.setLastPurchasePrice(rs.getBigDecimal("lastpurchaseprice"));
        productInventoryReport.setLastSalePrice(rs.getBigDecimal("lastsaleprice"));
        productInventoryReport.getLastPurchaseCurrency().setId(rs.getInt("lastpurchasecurrency_id"));
        productInventoryReport.getLastSaleCurrency().setId(rs.getInt("lastsalecurrency_id"));

        try {
            productInventoryReport.getStock().setName(rs.getString("stckname"));
            productInventoryReport.getStock().setCode(rs.getString("stckcode"));
            productInventoryReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            productInventoryReport.getStock().setBarcode(rs.getString("stckbarcode"));
            productInventoryReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            productInventoryReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            productInventoryReport.setQuantity(rs.getBigDecimal("quantity"));
            productInventoryReport.setWarehouseQuantity(rs.getBigDecimal("systemquantity"));
            productInventoryReport.setDifference(rs.getBigDecimal("different"));
            productInventoryReport.setDifferentStatus(rs.getInt("tef"));
            productInventoryReport.setPurchaseTaxRate(rs.getInt("purchasetaxgrouprate"));
            productInventoryReport.setSaleTaxRate(rs.getInt("salestaxgrouprate"));
            productInventoryReport.setSubCategories(rs.getString("subcategories"));
            productInventoryReport.setParentCategories(rs.getString("parentcategories"));
        } catch (Exception e) {

        }
        try {
            productInventoryReport.setPrice(rs.getBigDecimal("price"));
            productInventoryReport.getPriceCurrency().setId(rs.getInt("prlicurrency_id"));
            productInventoryReport.setSystemPrice(productInventoryReport.getWarehouseQuantity().multiply(productInventoryReport.getPrice()));
            productInventoryReport.setEnteredPrice(productInventoryReport.getQuantity().multiply(productInventoryReport.getPrice()));
            productInventoryReport.setDifferentPrice(productInventoryReport.getEnteredPrice().subtract(productInventoryReport.getSystemPrice()));
            
            
        } catch (Exception e) {
        }
        return productInventoryReport;
    }

}

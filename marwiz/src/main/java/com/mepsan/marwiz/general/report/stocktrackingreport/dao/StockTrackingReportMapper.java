/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 04:53:06
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockTrackingReportMapper implements RowMapper<StockTrackingReport> {
    
    @Override
    public StockTrackingReport mapRow(ResultSet rs, int i) throws SQLException {
        StockTrackingReport stockTrackingReport = new StockTrackingReport();
        stockTrackingReport.getStock().setId(rs.getInt("iwistock_id"));
        stockTrackingReport.getStock().setName(rs.getString("stckname"));
        stockTrackingReport.getStock().setBarcode(rs.getString("stckbarcode"));
        stockTrackingReport.getStock().setCode(rs.getString("stckcode"));
        stockTrackingReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        stockTrackingReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        stockTrackingReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        stockTrackingReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        stockTrackingReport.setQuantity(rs.getBigDecimal("sumquantity"));
        stockTrackingReport.setSalesPrice(rs.getBigDecimal("pllprice"));
        stockTrackingReport.setSalesPriceWithOutTax(rs.getBigDecimal("pllpricewithouttax"));
        stockTrackingReport.getCurrency().setId(rs.getInt("pllcurrency_id"));
        stockTrackingReport.setCategory(rs.getString("category"));
        stockTrackingReport.setCategory(StaticMethods.findCategories(stockTrackingReport.getCategory()));
        stockTrackingReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
        stockTrackingReport.getStock().getBrand().setName(rs.getString("brname"));
        stockTrackingReport.getStock().getSupplier().setId(rs.getInt("supplier_id"));
        stockTrackingReport.getStock().getSupplier().setName(rs.getString("accname"));
        stockTrackingReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        stockTrackingReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        stockTrackingReport.getBranch().setId(rs.getInt("brnid"));
        stockTrackingReport.getBranch().setName(rs.getString("brnname"));
        stockTrackingReport.setPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
        stockTrackingReport.setPurchasePriceWithTax(rs.getBigDecimal("sicurrentpurchasepricewithtax"));
        stockTrackingReport.getPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
      
        return stockTrackingReport;
    }
    
}

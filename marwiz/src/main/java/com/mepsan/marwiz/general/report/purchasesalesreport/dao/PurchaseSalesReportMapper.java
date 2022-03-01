/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.10.2018 13:56:23
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PurchaseSalesReportMapper implements RowMapper<PurchaseSalesReport> {

    @Override
    public PurchaseSalesReport mapRow(ResultSet rs, int i) throws SQLException {
        PurchaseSalesReport purchaseSalesReport = new PurchaseSalesReport();

        try {
            purchaseSalesReport.getStock().setId(rs.getInt("r_stock_id"));
            purchaseSalesReport.getStock().setCode(rs.getString("r_stckcode"));
            purchaseSalesReport.getStock().setName(rs.getString("r_stckname"));
            purchaseSalesReport.getStock().setBarcode(rs.getString("r_stckbarcode"));
            purchaseSalesReport.getStock().setCenterProductCode(rs.getString("r_stckcenterproductcode"));
            purchaseSalesReport.getStock().getUnit().setId(rs.getInt("r_stckunit_id"));
            purchaseSalesReport.getStock().getUnit().setSortName(rs.getString("r_guntsortname"));
            purchaseSalesReport.getStock().getUnit().setUnitRounding(rs.getInt("r_guntunitsorting"));
            purchaseSalesReport.getStocktaxgroup().setRate(rs.getBigDecimal("r_taxrate"));
            purchaseSalesReport.getCurrency().setId(rs.getInt("r_currency_id"));
            purchaseSalesReport.setQuantity(rs.getBigDecimal("r_quantity"));
            purchaseSalesReport.setTotalMoney(rs.getBigDecimal("r_totalmoney"));
            purchaseSalesReport.setTotalTax(rs.getBigDecimal("r_totaltax"));
            purchaseSalesReport.setTotalPrice(rs.getBigDecimal("r_totalprice"));
            purchaseSalesReport.setTotalDiscount(rs.getBigDecimal("r_totaldiscount"));
        } catch (Exception e) {
        }

        try {
            purchaseSalesReport.setUnitPrice(rs.getBigDecimal("r_unitprice"));
        } catch (Exception e) {
        }

        try {
            purchaseSalesReport.getBranchSetting().getBranch().setId(rs.getInt("r_brnid"));
            purchaseSalesReport.getBranchSetting().getBranch().setName(rs.getString("r_brnname"));
            purchaseSalesReport.setAvgPurchaseUnitPrice(rs.getBigDecimal("r_avgpurchaseunitprice"));
            purchaseSalesReport.setLastSaleUnitPrice(rs.getBigDecimal("r_lastsaleprice"));
            purchaseSalesReport.setLastPurchaseUnitPrice(rs.getBigDecimal("r_lastpurchaseprice"));
            purchaseSalesReport.setSalesTotalMoney(rs.getBigDecimal("r_salestotalmoney"));
            purchaseSalesReport.setAvgSaleUnitPrice(rs.getBigDecimal("r_avgsaleunitprice"));
            purchaseSalesReport.setPurchaseCost(rs.getBigDecimal("r_purchasecost"));
            purchaseSalesReport.setCost(rs.getBigDecimal("r_cost"));
            purchaseSalesReport.setCategory(rs.getString("r_category"));
            purchaseSalesReport.setCategory(StaticMethods.findCategories(purchaseSalesReport.getCategory()));
            purchaseSalesReport.getStock().getBrand().setId(rs.getInt("r_stckbrand_id"));
            purchaseSalesReport.getStock().getBrand().setName(rs.getString("r_brname"));
            purchaseSalesReport.getStock().getSupplier().setId(rs.getInt("r_supplier_id"));
            purchaseSalesReport.getStock().getSupplier().setName(rs.getString("r_accname"));
            purchaseSalesReport.getStock().getCentralSupplier().setId(rs.getInt("r_stckcentralsupplier_id"));
            purchaseSalesReport.getStock().getCentralSupplier().setName(rs.getString("r_csppname"));
            purchaseSalesReport.setStringResult(rs.getString("r_countresult"));

        } catch (Exception e) {
        }

        return purchaseSalesReport;
    }

}

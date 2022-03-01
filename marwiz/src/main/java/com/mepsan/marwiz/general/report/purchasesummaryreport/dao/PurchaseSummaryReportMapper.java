/**
 * This class ...
 *
 *
 * 
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:12:55
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PurchaseSummaryReportMapper implements RowMapper<PurchaseSummaryReport> {

    @Override
    public PurchaseSummaryReport mapRow(ResultSet rs, int i) throws SQLException {
        PurchaseSummaryReport purchaseSummaryReport = new PurchaseSummaryReport();

        try {
            purchaseSummaryReport.setId(rs.getInt("id"));
            purchaseSummaryReport.setTotalMoneyByStock(rs.getBigDecimal("totalmoneybystock"));
            purchaseSummaryReport.setTotalQuantityByStock(rs.getBigDecimal("totalcountbystock"));
        } catch (Exception e) {
        }
        try {
            purchaseSummaryReport.setCategory(rs.getString("category"));
            purchaseSummaryReport.setCategory(StaticMethods.findCategories(purchaseSummaryReport.getCategory()));
        } catch (Exception e) {
        }
        try {
            purchaseSummaryReport.getInvoice().setInvoiceDate(rs.getTimestamp("invinvoicedate"));
            purchaseSummaryReport.getInvoice().getStatus().setId(rs.getInt("invstatus_id"));
            purchaseSummaryReport.getInvoice().getType().setId(rs.getInt("invtype_id"));

        } catch (Exception e) {
        }

        purchaseSummaryReport.setTotalMoney(rs.getBigDecimal("invitotalmoney"));
        purchaseSummaryReport.getCurrency().setId(rs.getInt("invicurrency"));
        purchaseSummaryReport.setQuantity(rs.getBigDecimal("inviquantity"));
        try {
            purchaseSummaryReport.setPremiumAmount(rs.getBigDecimal("premiumamount"));

        } catch (Exception e) {
        }

        try {
            purchaseSummaryReport.getStock().setId(rs.getInt("invistock_id"));
            purchaseSummaryReport.getStock().setName(rs.getString("stckname"));
            purchaseSummaryReport.getStock().setBarcode(rs.getString("stckbarcode"));
            purchaseSummaryReport.getStock().setCode(rs.getString("stckcode"));
            purchaseSummaryReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            purchaseSummaryReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            purchaseSummaryReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            purchaseSummaryReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            purchaseSummaryReport.setUnitPriceWithTax(rs.getBigDecimal("inviunitprice"));

            purchaseSummaryReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            purchaseSummaryReport.getStock().getBrand().setName(rs.getString("brname"));
            purchaseSummaryReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
            purchaseSummaryReport.getStock().getSupplier().setName(rs.getString("accname"));
            purchaseSummaryReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            purchaseSummaryReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            purchaseSummaryReport.getStock().getStockInfo().setTurnoverPremium(rs.getBigDecimal("siturnoverpremium"));
            purchaseSummaryReport.setTaxRate(rs.getBigDecimal("invitaxrate"));

            BigDecimal unitPriceWithOutTax = new BigDecimal(BigInteger.ZERO);
            unitPriceWithOutTax = purchaseSummaryReport.getUnitPriceWithTax().divide((BigDecimal.valueOf(1).add(purchaseSummaryReport.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN);

            purchaseSummaryReport.setUnitPrice(unitPriceWithOutTax);
            

            
            
        } catch (Exception e) {
        }

        try {
            purchaseSummaryReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return purchaseSummaryReport;
    }

}

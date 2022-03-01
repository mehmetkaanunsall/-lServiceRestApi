package com.mepsan.marwiz.general.report.purchasedetailreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class PurchaseDetailReportMapper implements RowMapper<PurchaseDetailReport> {

    @Override
    public PurchaseDetailReport mapRow(ResultSet rs, int i) throws SQLException {
        PurchaseDetailReport purchaseDetailReport = new PurchaseDetailReport();
        purchaseDetailReport.setId(rs.getInt("inviid"));
        purchaseDetailReport.getCurrency().setId(rs.getInt("invicurrency"));
        purchaseDetailReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        purchaseDetailReport.setQuantity(rs.getBigDecimal("inviquantity"));
        purchaseDetailReport.setTotalMoney(rs.getBigDecimal("invitotalmoney"));
        purchaseDetailReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));

        try {
            purchaseDetailReport.getInvoice().getdNumber().setId(rs.getInt("invdocumentnumber_id"));
            purchaseDetailReport.getInvoice().getAccount().setName(rs.getString("invoiceaccount"));
            purchaseDetailReport.getInvoice().setDocumentSerial(rs.getString("invdocumentserial"));
            purchaseDetailReport.getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));
            purchaseDetailReport.getInvoice().getStatus().setId(rs.getInt("invstatus_id"));
            purchaseDetailReport.getInvoice().getType().setId(rs.getInt("invtype_id"));
            purchaseDetailReport.getStock().setId(rs.getInt("invistock_id"));
            purchaseDetailReport.getStock().setName(rs.getString("stckname"));
            purchaseDetailReport.getInvoice().setInvoiceDate(rs.getTimestamp("invinvoicedate"));
            purchaseDetailReport.getStock().setBarcode(rs.getString("stckbarcode"));
            purchaseDetailReport.getStock().setCode(rs.getString("stckcode"));
            purchaseDetailReport.setCategory(rs.getString("category"));
            purchaseDetailReport.setCategory(StaticMethods.findCategories(purchaseDetailReport.getCategory()));
            purchaseDetailReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));

            purchaseDetailReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            purchaseDetailReport.setUnitPrice(rs.getBigDecimal("inviunitprice"));

            purchaseDetailReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            purchaseDetailReport.getStock().getBrand().setName(rs.getString("brname"));
            purchaseDetailReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
            purchaseDetailReport.getStock().getSupplier().setName(rs.getString("accname"));
            purchaseDetailReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            purchaseDetailReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            purchaseDetailReport.setTaxRate(rs.getBigDecimal("invitaxrate"));
            purchaseDetailReport.setTotalTax(rs.getBigDecimal("invitotaltax"));
        } catch (Exception e) {
        }
        try {
            purchaseDetailReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }
        return purchaseDetailReport;
    }

}

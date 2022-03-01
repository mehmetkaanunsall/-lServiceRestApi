/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2020 05:54:27
 */
package com.mepsan.marwiz.general.report.freestockreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FreeStockReportMapper implements RowMapper<FreeStockReport> {

    @Override
    public FreeStockReport mapRow(ResultSet rs, int i) throws SQLException {
        FreeStockReport freeStockReport = new FreeStockReport();

        freeStockReport.setId(rs.getInt("inviid"));
        freeStockReport.getInvoice().setInvoiceDate(rs.getTimestamp("invinvoicedate"));
        freeStockReport.getStock().setId(rs.getInt("invistock_id"));
        freeStockReport.getStock().setCode(rs.getString("stckcode"));
        freeStockReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        freeStockReport.getStock().setBarcode(rs.getString("stckbarcode"));
        freeStockReport.getStock().setName(rs.getString("stckname"));
        freeStockReport.setCategory(rs.getString("category"));
        freeStockReport.setCategory(StaticMethods.findCategories(freeStockReport.getCategory()));
        freeStockReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
        freeStockReport.getStock().getSupplier().setName(rs.getString("accname"));
        freeStockReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        freeStockReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        freeStockReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
        freeStockReport.getStock().getBrand().setName(rs.getString("brname"));
        freeStockReport.getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));
        freeStockReport.getInvoice().setDocumentSerial(rs.getString("invdocumentserial"));
        freeStockReport.setQuantity(rs.getBigDecimal("inviquantity"));
        freeStockReport.getInvoice().setIsPurchase(rs.getBoolean("invis_purchase"));
        freeStockReport.getInvoice().getType().setId(rs.getInt("invtype_id"));
        freeStockReport.getStock().getUnit().setId(rs.getInt("inviunit_id"));
        freeStockReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        freeStockReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        
        try {
            freeStockReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return freeStockReport;
    }

}

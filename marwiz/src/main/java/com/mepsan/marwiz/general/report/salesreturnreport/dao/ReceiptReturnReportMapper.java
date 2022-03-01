/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:40:02
 */
package com.mepsan.marwiz.general.report.salesreturnreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ReceiptReturnReportMapper implements RowMapper<ReceiptReturnReport> {

    @Override
    public ReceiptReturnReport mapRow(ResultSet rs, int i) throws SQLException {
        ReceiptReturnReport receiptReturnReport = new ReceiptReturnReport();

        receiptReturnReport.setId(rs.getInt("slid"));
        try {
            receiptReturnReport.getSales().getReceipt().setReceiptNo(rs.getString("rcpreceiptno"));
            receiptReturnReport.getSales().getInvoice().setId(rs.getInt("invid"));
            receiptReturnReport.getSales().getInvoice().setDocumentNumber(rs.getString("invdocumentno"));
            receiptReturnReport.setProcessDate(rs.getTimestamp("slprocessdate"));
            receiptReturnReport.getStock().setId(rs.getInt("slistock_id"));
            receiptReturnReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            receiptReturnReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            receiptReturnReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
            receiptReturnReport.getStock().setName(rs.getString("stckname"));
            receiptReturnReport.getStock().setCode(rs.getString("stckcode"));
            receiptReturnReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            receiptReturnReport.getStock().setBarcode(rs.getString("stckbarcode"));
             receiptReturnReport.getSales().getReceipt().setId(rs.getInt("rcpid"));
        } catch (Exception e) {
        }
       

        receiptReturnReport.setTotalMoney(rs.getBigDecimal("slitotalmoney"));
        receiptReturnReport.getCurrency().setId(rs.getInt("slicurrency_id"));
        receiptReturnReport.setQuantity(rs.getBigDecimal("sliquantity"));
      
            receiptReturnReport.getBranch().setId(rs.getInt("brnid"));
            receiptReturnReport.getBranch().setName(rs.getString("brnname"));
       

        try {
            receiptReturnReport.setTotalPrice(rs.getBigDecimal("slitotalprice"));
        } catch (Exception e) {
        }
        try {
            receiptReturnReport.setTaxRate(rs.getBigDecimal("stgrate"));

        } catch (Exception e) {
        }
        return receiptReturnReport;
    }
}

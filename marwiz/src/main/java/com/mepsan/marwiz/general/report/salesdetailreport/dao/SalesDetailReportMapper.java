/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 12:09:12
 */
package com.mepsan.marwiz.general.report.salesdetailreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesDetailReportMapper implements RowMapper<SalesDetailReport> {

    @Override
    public SalesDetailReport mapRow(ResultSet rs, int i) throws SQLException {

        SalesDetailReport salesDetailReport = new SalesDetailReport();

        try {
            salesDetailReport.getBranchSetting().getBranch().setId(rs.getInt("brnid"));
            salesDetailReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));

            salesDetailReport.getStock().setId(rs.getInt("slistock_id"));
            salesDetailReport.getStock().setName(rs.getString("stckname"));
            salesDetailReport.getStock().setBarcode(rs.getString("stckbarcode"));
            salesDetailReport.getStock().setCode(rs.getString("stckcode"));
            salesDetailReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            salesDetailReport.setProcessDate(rs.getTimestamp("sliprocessdate"));
            salesDetailReport.getSales().getReceipt().setId(rs.getInt("slreceipt_id"));
            salesDetailReport.getSales().getReceipt().setReceiptNo(rs.getString("rcpreceiptno"));
            salesDetailReport.setUnitPrice(rs.getBigDecimal("sliunitprice"));

            salesDetailReport.getSales().getAccount().setId(rs.getInt("slaccount_id"));
            salesDetailReport.getSales().getAccount().setName(rs.getString("accname"));
            salesDetailReport.getSales().getAccount().setTitle(rs.getString("acctitle"));
            salesDetailReport.getSales().getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            salesDetailReport.getSales().getInvoice().setId(rs.getInt("slinvoice_id"));
            salesDetailReport.getSales().getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));

            salesDetailReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));

            salesDetailReport.setCategory(rs.getString("category"));
            salesDetailReport.setCategory(StaticMethods.findCategories(salesDetailReport.getCategory()));
            salesDetailReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            salesDetailReport.getStock().getBrand().setName(rs.getString("brname"));
            salesDetailReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
            salesDetailReport.getStock().getSupplier().setName(rs.getString("acc1name"));
            salesDetailReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            salesDetailReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            salesDetailReport.getSales().getSaleType().setId(rs.getInt("slsaletype_id"));
            salesDetailReport.getSales().getDiscountType().setId(rs.getInt("sldiscounttype_id"));
            salesDetailReport.getSales().setTransactionNo(rs.getString("sltransactionno"));

        } catch (Exception e) {
        }

        try {
            salesDetailReport.setId(rs.getInt("sliid"));
            salesDetailReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            salesDetailReport.getCurrency().setId(rs.getInt("slicurrency_id"));
            salesDetailReport.setQuantity(rs.getBigDecimal("sliquantity"));
            salesDetailReport.setTotalMoney(rs.getBigDecimal("slitotalmoney"));
        } catch (Exception e) {
        }

        try {
            salesDetailReport.getStock().getUnit().setId(rs.getInt("sliunit_id"));
        } catch (Exception e) {
        }

        try {
            salesDetailReport.getSales().getUser().setName(rs.getString("usname"));
            salesDetailReport.getSales().getUser().setSurname(rs.getString("ussurname"));
        } catch (Exception e) {
        }

        
        return salesDetailReport;
    }

}

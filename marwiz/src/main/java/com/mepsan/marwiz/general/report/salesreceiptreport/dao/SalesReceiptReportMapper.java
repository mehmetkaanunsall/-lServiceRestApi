/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 01:33:39
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesReceiptReportMapper implements RowMapper<SalesReport> {

    @Override
    public SalesReport mapRow(ResultSet rs, int i) throws SQLException {
        SalesReport salesReport = new SalesReport();
        salesReport.setId(rs.getInt("slid"));
        salesReport.setTotalMoney(rs.getBigDecimal("sltotalmoney"));
        salesReport.getCurrency().setId(rs.getInt("slcurrency_id"));
        salesReport.setTotalDiscount(rs.getBigDecimal("sltotaldiscount"));

        try {
            salesReport.setProcessDate(rs.getTimestamp("slprocessdate"));
            salesReport.getAccount().setId(rs.getInt("slaccount_id"));
            salesReport.getAccount().setName(rs.getString("accname"));
            salesReport.getAccount().setTitle(rs.getString("acctitle"));
            salesReport.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            salesReport.getReceipt().setId(rs.getInt("slreceipt_id"));
            salesReport.getReceipt().setReceiptNo(rs.getString("rcpreceiptno"));
            salesReport.setTotalTax(rs.getBigDecimal("sltotaltax"));
            salesReport.setSaleDate(rs.getDate("saledate"));
            salesReport.getShift().setId(rs.getInt("slshift_id"));
            salesReport.setShiftNo(rs.getString("slshiftno"));
            salesReport.getPointOfSale().setId(rs.getInt("slpointofsale_id"));
            salesReport.setPosMacAddress(rs.getString("slposmacaddress"));
            salesReport.getPointOfSale().setName(rs.getString("posname"));
            salesReport.setTotalPrice(rs.getBigDecimal("sltotalprice"));
            salesReport.getInvoice().setId(rs.getInt("slinvoice_id"));
            salesReport.getInvoice().setDocumentNumber(rs.getString("invdocumnetnumber"));
            salesReport.getUser().setId(rs.getInt("sluserdata_id"));
            salesReport.getUser().setName(rs.getString("usrname"));
            salesReport.getUser().setSurname(rs.getString("usrsurname"));
            salesReport.getSaleType().setId(rs.getInt("slsaletype_id"));
            salesReport.setTransactionNo(rs.getString("sltransactionno"));
            salesReport.getDiscountType().setId(rs.getInt("sldiscounttype_id"));
            salesReport.getDiscountType().setTag(rs.getString("typdname"));
            salesReport.setIsOnline(rs.getBoolean("slis_online"));
            salesReport.setDiscountPrice(rs.getBigDecimal("sldiscountprice"));
        } catch (Exception e) {
        }

        try {
            salesReport.setSubTotalCount(rs.getInt("slidcount"));
            salesReport.setSubTotalMoney(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }

        try {

            salesReport.getBranchSetting().getBranch().setId(rs.getInt("brnid"));
            salesReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }
        try {
            salesReport.setDiscountCount(rs.getInt("discountcount"));
            salesReport.setCardOperationCount(rs.getInt("cardoperationcount"));
        } catch (Exception e) {
        }

        return salesReport;
    }

}

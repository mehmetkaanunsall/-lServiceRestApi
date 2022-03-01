/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 03:59:17
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatSalesReportMapper implements RowMapper<AutomatSalesReport> {

    @Override
    public AutomatSalesReport mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSalesReport automatSalesReport = new AutomatSalesReport();
        automatSalesReport.setId(rs.getInt("aslid"));
        automatSalesReport.setTotalMoney(rs.getBigDecimal("asltotalmoney"));
        automatSalesReport.getCurrency().setId(rs.getInt("aslcurrency_id"));

        try {
            automatSalesReport.setSaleDate(rs.getDate("aslsaledate"));
            automatSalesReport.setSaleDateTime(rs.getTimestamp("aslsaledatetime"));
            automatSalesReport.setPaymentType(rs.getInt("aslpaymenttype_id"));
            automatSalesReport.setDiscountPrice(rs.getBigDecimal("asltotaldiscount"));
            automatSalesReport.setTaxPrice(rs.getBigDecimal("asltotaltax"));
            automatSalesReport.setTotalPrice(rs.getBigDecimal("asltotalprice"));
            automatSalesReport.getShift().setId(rs.getInt("aslshift_id"));
            automatSalesReport.getShift().setShiftNo(rs.getString("aslshiftno"));
            automatSalesReport.getWashingMachine().setId(rs.getInt("aslwashingmachine_id"));
            automatSalesReport.getWashingMachine().setName(rs.getString("wshname"));
            automatSalesReport.setMacAddress(rs.getString("aslmacaddress"));
            automatSalesReport.getPlatform().setId(rs.getInt("aslplatform_id"));
            automatSalesReport.setPlatformNo(rs.getString("aslplatformno"));
        } catch (Exception e) {
        }

        try {
            automatSalesReport.setSubTotalCount(rs.getInt("aslidcount"));
            automatSalesReport.setSubTotalMoney(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }

        try {
            automatSalesReport.setIsOnline(rs.getBoolean("aslis_online"));
            automatSalesReport.setBarcodeNo(rs.getString("aslbarcodeno"));
            automatSalesReport.setCustomerRfid(rs.getString("aslcustomerrfid"));
            automatSalesReport.setMobileNo(rs.getString("aslmobileno"));
            automatSalesReport.getAccount().setId(rs.getInt("aslaccount_id"));
            automatSalesReport.setOperationTime(rs.getInt("asloperationtime"));

        } catch (Exception e) {
        }
        try {
            automatSalesReport.getStock().setId(rs.getInt("aslstock_id"));
            automatSalesReport.getStock().setName(rs.getString("stckname"));
        } catch (Exception e) {
        }

        return automatSalesReport;
    }

}

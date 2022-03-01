/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author tugcan.koparan
 */
public class FuelShiftReportMapper implements RowMapper<FuelShiftReport> {

    @Override
    public FuelShiftReport mapRow(ResultSet rs, int i) throws SQLException {
        FuelShiftReport fuelShiftReport = new FuelShiftReport();
        fuelShiftReport.setId(rs.getInt("shfid"));
        try {
            fuelShiftReport.setBeginDate(rs.getTimestamp("shfbegindate"));
            fuelShiftReport.setEndDate(rs.getTimestamp("shfenddate"));
            fuelShiftReport.setShiftNo(rs.getString("shfshiftno"));
        } catch (Exception e) {
        }

        fuelShiftReport.setNumberofVehicle(rs.getInt("salecount"));
        fuelShiftReport.setTotalSalesAmount(rs.getBigDecimal("ssltotalamount"));
        fuelShiftReport.setTotalSalesPrice(rs.getBigDecimal("ssltotalprice"));
        fuelShiftReport.setIncomePrice(rs.getBigDecimal("iemincomeprices"));
        fuelShiftReport.setExpensePrice(rs.getBigDecimal("iemexpenseprices"));
        fuelShiftReport.setDeficitSurplusPrice(rs.getBigDecimal("deficitsurplus"));
        fuelShiftReport.setCreditCardPaymentPrice(rs.getBigDecimal("shpcreditcardpaymentprice"));
        fuelShiftReport.setCreditPaymentPrice(rs.getBigDecimal("creditpaymentprice"));
        fuelShiftReport.setCashPaymentPrice(rs.getBigDecimal("cashpaymentprice"));
        fuelShiftReport.setTtsPaymentPrice(rs.getBigDecimal("ttspaymentprice"));

        try {
            fuelShiftReport.setParoPaymentPrice(rs.getBigDecimal("paropaymentprice"));
            fuelShiftReport.setDkvPaymentPrice(rs.getBigDecimal("dkvpaymentprice"));
            fuelShiftReport.setUtaPaymentPrice(rs.getBigDecimal("utapaymentprice"));
            fuelShiftReport.setPresentPaymentPrice(rs.getBigDecimal("presentpaymentprice"));
            fuelShiftReport.setFuelCardPaymentPrice(rs.getBigDecimal("fuelcardpaymentprice"));
        } catch (Exception e) {
        }
        return fuelShiftReport;
    }

}

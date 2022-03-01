/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 14:11:29
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FuelShiftMapper implements RowMapper<FuelShift> {

    @Override
    public FuelShift mapRow(ResultSet rs, int i) throws SQLException {
        FuelShift fs = new FuelShift();
        try {
            fs.setId(rs.getInt("shfid"));
        } catch (Exception e) {
        }
        try {
            fs.setShiftNo(rs.getString("shfshiftno"));
            fs.setBeginDate(rs.getTimestamp("shfbegindate"));
            fs.setEndDate(rs.getTimestamp("shfenddate"));
        } catch (Exception e) {
        }
        try {
            fs.setTotalMoney(rs.getBigDecimal("sslprice"));
        } catch (Exception e) {
        }
        try {
            fs.setShiftPaymentTotal(rs.getBigDecimal("shiftpaymenttotal"));
        } catch (Exception e) {
        }
        try {
            fs.setShiftAttendant(rs.getString("shiftattendant"));
            fs.setIsConfirm(rs.getBoolean("shfis_confirm"));
            fs.setIsDeleted(rs.getBoolean("deleted"));
            fs.setDeletedTime(rs.getTimestamp("shfd_time"));
        } catch (Exception e) {
        }
        try {
            fs.setId(rs.getInt("r_result_id"));
            fs.setIncorrectRecord(rs.getString("r_resultmessage"));
        } catch (Exception e) {

        }
        try {
            fs.setIncorrectRecord(rs.getString("shferrordata"));
        } catch (Exception e) {
        }

        try {
            fs.setTotalSalesAmount(rs.getBigDecimal("ssltotalamount"));
            fs.setCreditCardPaymentPrice(rs.getBigDecimal("shpcreditcardpaymentprice"));
            fs.setCreditPaymentPrice(rs.getBigDecimal("creditpaymentprice"));
            fs.setTtsPaymentPrice(rs.getBigDecimal("ttspaymentprice"));
        } catch (Exception e) {
        }

        return fs;
    }

}

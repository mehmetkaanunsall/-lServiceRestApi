/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 03:05:17
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.Shift;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftReportMapper implements RowMapper<Shift> {

    @Override
    public Shift mapRow(ResultSet rs, int i) throws SQLException {
        Shift shift = new Shift();
        try {
            shift.setId(rs.getInt("shfid"));
            shift.setShiftNo(rs.getString("shfshiftno"));
            shift.setBeginDate(rs.getTimestamp("shfbegindate"));
            shift.setEndDate(rs.getTimestamp("shfenddate"));
        } catch (Exception e) {
        }
        try {
            shift.getStatus().setId(rs.getInt("shfstatus_id"));
            shift.setSaleCount(rs.getInt("slcount"));
        } catch (Exception e) {
        }
        try {
            shift.setIs_ShiftPaymentCheck(rs.getBoolean("isshiftpaymentcheck"));
            shift.setIs_Confirm(rs.getBoolean("sfis_confirm"));
            shift.setIs_MovementSafe(rs.getBoolean("ismovementsafe"));
        } catch (Exception e) {
        }

        return shift;

    }

}

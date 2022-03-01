/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:42:01
 */
package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.model.general.Shift;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftMapper implements RowMapper<Shift> {

    @Override
    public Shift mapRow(ResultSet rs, int i) throws SQLException {
        Shift shift = new Shift();
        shift.setId(rs.getInt("shfid"));

        try {
            shift.setShiftNo(rs.getString("shfshiftno"));
        } catch (Exception e) {
        }

        try {
            shift.setName(rs.getString("shfname"));
        } catch (Exception e) {
        }
        try {
            shift.setBeginDate(rs.getTimestamp("shfbegindate"));
            shift.setEndDate(rs.getTimestamp("shfenddate"));
        } catch (Exception e) {
        }

        try {
            shift.getStatus().setId(rs.getInt("shfstatus_id"));
            shift.getStatus().setTag(rs.getString("sttdname"));
            shift.setTotalSaleAmountString(rs.getString("totalsaleamount"));
            shift.setIs_Confirm(rs.getBoolean("shfis_confirm"));
            shift.setSumOfRemovedStock(rs.getBigDecimal("removedstock"));
            shift.setActualPriceString(rs.getString("actualprice"));
            shift.setIsAvailableSale(rs.getBoolean("isAvailableSale"));
            shift.setShiftPerson(rs.getString("shiftperson"));
        } catch (Exception e) {
        }

        return shift;
    }

}

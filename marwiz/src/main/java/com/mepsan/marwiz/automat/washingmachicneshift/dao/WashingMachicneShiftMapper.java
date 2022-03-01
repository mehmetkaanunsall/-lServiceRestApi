/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:19:05 PM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.dao;

import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicneShiftMapper implements RowMapper<AutomatShift> {

    @Override
    public AutomatShift mapRow(ResultSet rs, int i) throws SQLException {
        AutomatShift washingMachicneShift = new AutomatShift();

        washingMachicneShift.setId(rs.getInt("shfid"));
        washingMachicneShift.setShiftNo(rs.getString("shfshiftno"));

        try {
            washingMachicneShift.setBeginDate(rs.getTimestamp("shfbegindate"));
            washingMachicneShift.setEndDate(rs.getTimestamp("shfenddate"));
            washingMachicneShift.getStatus().setId(rs.getInt("shfstatus_id"));
            washingMachicneShift.getStatus().setTag(rs.getString("sttdname"));
        } catch (Exception e) {
        }

        return washingMachicneShift;
    }

}

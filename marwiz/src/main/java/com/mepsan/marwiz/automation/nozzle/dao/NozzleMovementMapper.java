/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:27:27
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class NozzleMovementMapper implements RowMapper<NozzleMovement> {

    @Override
    public NozzleMovement mapRow(ResultSet rs, int i) throws SQLException {
        NozzleMovement nzm = new NozzleMovement();

        nzm.setId(rs.getInt("shsid"));
        nzm.setProcessDate(rs.getTimestamp("shsprocessdate"));
        nzm.setReceiptNo(rs.getString("shsreceiptno"));
        nzm.setDifferentIndex(rs.getBigDecimal("shsliter"));
        nzm.getNozzle().setId(rs.getInt("shsnozzle_id"));
        nzm.getNozzle().setName(rs.getString("nzname"));
        nzm.getNozzle().setNozzleNo(rs.getString("nznozzleno"));
        nzm.getFuelShift().setId(rs.getInt("shsshift_id"));
        nzm.getFuelShift().setShiftNo(rs.getString("shfshiftno"));
        nzm.getStock().setId(rs.getInt("shsstock_id"));
        nzm.getStock().setName(rs.getString("stckname"));
        nzm.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        nzm.getStock().getUnit().setName(rs.getString("guntname"));
        nzm.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        nzm.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        nzm.getUserCreated().setId(rs.getInt("shsc_id"));
        nzm.getUserCreated().setName(rs.getString("usname"));
        nzm.getUserCreated().setSurname(rs.getString("ussurname"));
        return nzm;

    }

}

/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:01:55 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingTank;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicneTankMapper implements RowMapper<WashingTank> {

    @Override
    public WashingTank mapRow(ResultSet rs, int i) throws SQLException {
        WashingTank washingMachicneTank = new WashingTank();

        washingMachicneTank.setId(rs.getInt("tnkid"));
        washingMachicneTank.setTankNo(rs.getString("tnktankno"));

        try {
            washingMachicneTank.setBalance(rs.getBigDecimal("tnkbalance"));
            washingMachicneTank.setCapacity(rs.getBigDecimal("tnkcapacity"));
            washingMachicneTank.getStock().setId(rs.getInt("tnkstockid"));
            washingMachicneTank.setMinCapacity(rs.getBigDecimal("tnkmincapacity"));
            washingMachicneTank.getWashingMachicne().setId(rs.getInt("tnkwashingmachine_id"));
            washingMachicneTank.getStock().setName(rs.getString("stckname"));
        } catch (Exception e) {
        }

        try {
            washingMachicneTank.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            washingMachicneTank.getStock().getUnit().setSortName(rs.getString("untsortname"));
            washingMachicneTank.getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
        } catch (Exception e) {
        }
        return washingMachicneTank;
    }

}

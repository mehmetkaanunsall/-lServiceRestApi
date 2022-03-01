/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:41:28 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicneNozzleMapper implements RowMapper<WashingNozzle> {

    @Override
    public WashingNozzle mapRow(ResultSet rs, int i) throws SQLException {
        WashingNozzle nozzle = new WashingNozzle();

        nozzle.setId(rs.getInt("nzid"));
        nozzle.setNozzleNo(rs.getString("nznozzleno"));

        try {
            nozzle.getWashingMachicnePlatform().setId(rs.getInt("nzplatformid"));
            nozzle.getWashingMachicneTank().setId(rs.getInt("nztankid"));
            nozzle.getWashingMachicneTank().setTankNo(rs.getString("tnktankno"));
            nozzle.setOperationAmount(rs.getBigDecimal("nzoperationamount"));
            nozzle.setOperationTime(rs.getInt("nzoperationtime"));
            nozzle.setUnitPrice(rs.getBigDecimal("nzunitprice"));
            nozzle.getCurrency().setId(rs.getInt("nzcurrency_id"));
            nozzle.getCurrency().setCode(rs.getString("crycode"));
            nozzle.getUnit().setId(rs.getInt("stckunitid"));
            nozzle.getUnit().setSortName(rs.getString("untsortname"));
            nozzle.getUnit().setName(rs.getString("untname"));
            nozzle.getUnit().setUnitRounding(rs.getInt("untunitrounding"));
            nozzle.setStockName(rs.getString("stckname"));
            nozzle.setElectricAmount(rs.getBigDecimal("nzelectricamount"));
            nozzle.setWaterAmount(rs.getBigDecimal("nzwateramount"));
        } catch (Exception e) {
        }
        return nozzle;
    }

}

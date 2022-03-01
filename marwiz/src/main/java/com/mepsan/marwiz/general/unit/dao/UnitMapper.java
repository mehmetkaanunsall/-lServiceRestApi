/**
 *
 * Bu sınıf, Unit nesnesini oluşturur ve özelliklerini set eder.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:52:45
 */
package com.mepsan.marwiz.general.unit.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UnitMapper implements RowMapper<Unit> {

    @Override
    public Unit mapRow(ResultSet rs, int i) throws SQLException {
        Unit unit = new Unit();

        unit.setId(rs.getInt("guntid"));
        unit.setName(rs.getString("guntname"));
        unit.setSortName(rs.getString("guntsortname"));
        unit.setUnitRounding(rs.getInt("guntunitrounding"));
        try {
            unit.setInternationalCode(rs.getString("guntinternationalcode"));
        } catch (Exception e) {
        }
        
        try {
            unit.setCenterunit_id(rs.getInt("guntcenterunit_id"));

        } catch (Exception e) {

        }

        try {
            unit.setIntegrationCode(rs.getString("guntintegrationcode"));
        } catch (Exception e) {
        }
        try {
            unit.setMainWeightUnit(new Unit());
            unit.setMainWeight(rs.getBigDecimal("guntmainweight"));
            unit.getMainWeightUnit().setId(rs.getInt("guntmainweightunit_id"));
            unit.getMainWeightUnit().setName(rs.getString("gunt2name"));
            unit.getMainWeightUnit().setSortName(rs.getString("gunt2sortname"));
            unit.getMainWeightUnit().setUnitRounding(rs.getInt("gunt2unitrounding"));
        } catch (Exception e) {
        }

        return unit;
    }

}

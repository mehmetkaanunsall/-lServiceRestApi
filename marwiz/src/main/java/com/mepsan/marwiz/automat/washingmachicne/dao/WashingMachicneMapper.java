/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:02:26 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicneMapper implements RowMapper<WashingMachicne> {

    @Override
    public WashingMachicne mapRow(ResultSet rs, int i) throws SQLException {
        WashingMachicne washingMachicne = new WashingMachicne();

        washingMachicne.setId(rs.getInt("wmid"));
        washingMachicne.setCode(rs.getString("wmcode"));
        washingMachicne.setName(rs.getString("wmname"));
        try {
            washingMachicne.setIpAddress(rs.getString("wmipaddress"));
            washingMachicne.setMacAddress(rs.getString("wmmacaddress"));
            washingMachicne.setVersion(rs.getString("wmversion"));
            washingMachicne.setDescription(rs.getString("wmdescription"));

            washingMachicne.getStatus().setId(rs.getInt("wmstatus_id"));
            washingMachicne.getStatus().setTag(rs.getString("sttdname"));
            washingMachicne.setPort(rs.getString("wmport"));
            washingMachicne.setElectricUnitPrice(rs.getBigDecimal("wmelectricunitprice"));
            washingMachicne.setWaterUnitPrice(rs.getBigDecimal("wmwaterunitprice"));
        } catch (Exception e) {
        }

        return washingMachicne;
    }
}

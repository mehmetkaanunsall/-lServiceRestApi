/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:27:27
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import com.mepsan.marwiz.general.model.automation.Nozzle;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class NozzleMapper implements RowMapper<Nozzle> {

    @Override
    public Nozzle mapRow(ResultSet rs, int i) throws SQLException {
        Nozzle nz = new Nozzle();
        nz.setId(rs.getInt("nzid"));
        nz.getWarehouse().setId(rs.getInt("nzwarehouse_id"));
        nz.getWarehouse().setName(rs.getString("wrname"));
        nz.setName(rs.getString("nzname"));
        nz.setPumpNo(rs.getString("nzpumpno"));
        nz.setNozzleNo(rs.getString("nznozzleno"));
        nz.getStatus().setId(rs.getInt("nzstatus_id"));
        nz.getStatus().setTag(rs.getString("sttdname"));
        nz.setIndex(rs.getBigDecimal("nzindex"));
        nz.setIsAscending(rs.getBoolean("nzis_ascending"));
        nz.setDescription(rs.getString("nzdescription"));
        return nz;

    }

}

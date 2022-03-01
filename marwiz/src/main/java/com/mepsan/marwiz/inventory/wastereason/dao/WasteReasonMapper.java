package com.mepsan.marwiz.inventory.wastereason.dao;

import com.mepsan.marwiz.general.model.inventory.WasteReason;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class WasteReasonMapper implements RowMapper<WasteReason> {

    @Override
    public WasteReason mapRow(ResultSet rs, int i) throws SQLException {

        WasteReason wasteReason = new WasteReason();

        wasteReason.setId(rs.getInt("iwreid"));
        wasteReason.setName(rs.getString("iwrename"));
        wasteReason.setCenterwastereason_id(rs.getInt("iwrecenterwastereason_id"));

        return wasteReason;
    }

}

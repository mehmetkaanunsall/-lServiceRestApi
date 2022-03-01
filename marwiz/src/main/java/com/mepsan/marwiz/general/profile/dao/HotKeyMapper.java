/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:08:32
 */
package com.mepsan.marwiz.general.profile.dao;

import com.mepsan.marwiz.general.model.general.HotKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class HotKeyMapper implements RowMapper<HotKey> {

    @Override
    public HotKey mapRow(ResultSet rs, int rowNum) throws SQLException {
        HotKey hotKey = new HotKey();

        hotKey.setId(rs.getInt("uhkid"));
        hotKey.setHotkey(rs.getString("uhkhotkey"));
        hotKey.getPage().setId(rs.getInt("uhkpage_id"));
        hotKey.getPage().setName(rs.getString("pgedname"));
        hotKey.getPage().setUrl(rs.getString("pgeurl"));
        return hotKey;
    }

}

/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 22.03.2019 14:05:24
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomationConfigureMapper implements RowMapper<String> {

    @Override
    public String mapRow(ResultSet rs, int i) throws SQLException {
        String result = rs.getString("r_result");
        return result;
    }

}

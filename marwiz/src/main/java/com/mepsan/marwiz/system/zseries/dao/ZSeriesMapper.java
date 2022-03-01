/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.zseries.dao;

import com.mepsan.marwiz.general.model.general.ZSeries;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author m.duzoylum
 */
public class ZSeriesMapper implements RowMapper<ZSeries> {

    @Override
    public ZSeries mapRow(ResultSet rs, int i) throws SQLException {
        ZSeries zseries = new ZSeries();

        zseries.setId(rs.getInt("id"));
        zseries.setType(rs.getInt("type_id"));
        zseries.setNumber(rs.getString("number"));

        return zseries;

    }

}

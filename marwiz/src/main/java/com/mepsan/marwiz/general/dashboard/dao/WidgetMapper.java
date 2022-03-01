/**
 * Bu sınıf Widget nesnesinin adreslenmesi için yazılmıştır.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   22.08.2016 11:03
 * 
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.Widget;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WidgetMapper implements RowMapper<Widget> {

    @Override
    public Widget mapRow(ResultSet rs, int i) throws SQLException {
        Widget dashboard= new Widget();
        dashboard.setId(rs.getInt("id"));
        dashboard.setName(rs.getString("name"));
        return dashboard;
    }
    
}

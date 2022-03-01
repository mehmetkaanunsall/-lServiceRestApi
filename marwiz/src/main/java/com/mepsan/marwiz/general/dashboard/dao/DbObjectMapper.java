/**
 * Bu sınıf WidgetUserDataConMapper nesnesinin adreslenmesi için yazılmıştır.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   07.09.2016 09:32
 * @edited Zafer Yaşar - aliaslar için düzeltildi.
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.admin.DbObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DbObjectMapper implements RowMapper<DbObject> {

    @Override
    public DbObject mapRow(ResultSet rs, int i) throws SQLException {
        DbObject dbObject= new DbObject();
        dbObject.setId(rs.getInt("id"));
        dbObject.setName(rs.getString("name"));
        dbObject.setType(rs.getInt("type"));
        dbObject.setTag(rs.getString("tag"));
        return dbObject;
    }
}

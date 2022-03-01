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

import com.mepsan.marwiz.general.model.general.Widget;
import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WidgetUserDataConMapper implements RowMapper<WidgetUserDataCon> {

    @Override
    public WidgetUserDataCon mapRow(ResultSet rs, int i) throws SQLException {
        WidgetUserDataCon widgetUserDataCon= new WidgetUserDataCon();
        Widget widget= new Widget();
        UserData userData =new UserData();
        widgetUserDataCon.setId(rs.getInt("wducid"));
        widgetUserDataCon.setCol(rs.getInt("col"));
        widgetUserDataCon.setRow(rs.getInt("row"));
        widget.setId(rs.getInt("id"));
        widget.setName(rs.getString("name"));
        userData.setId(rs.getInt("userdata_id"));
        widgetUserDataCon.setWidget(widget);
        widgetUserDataCon.setUserData(userData);
        return widgetUserDataCon;
    }
}

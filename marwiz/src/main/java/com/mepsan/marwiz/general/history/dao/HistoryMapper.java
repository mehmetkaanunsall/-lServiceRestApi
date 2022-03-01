/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date  13.01.2017 08:07:43
 */
package com.mepsan.marwiz.general.history.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.History;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class HistoryMapper implements RowMapper<History> {

    @Override
    public History mapRow(ResultSet rs, int i) throws SQLException {

        History history = new History();
        history.setId(rs.getInt("hisid"));
        history.setProcessType(rs.getString("hisaction"));
        history.getUserData().setId(rs.getInt("hisuserdata_id"));
        history.getUserData().setUsername(rs.getString("usrusername"));
        history.getUserData().setSurname(rs.getString("usrsurname"));
        history.getUserData().setName(rs.getString("usrname"));
        if (rs.getString("hiscolumnname")!=null && rs.getString("hiscolumnname").length() > 7) {
            if ((rs.getString("hiscolumnname").substring(rs.getString("hiscolumnname").length() - 7)).equals("deleted")) {
                history.setColumnName("deleted");
            } else {
                history.setColumnName(rs.getString("hiscolumnname"));
            }
        } else {
            history.setColumnName(rs.getString("hiscolumnname"));
        }
        history.setOldValue(rs.getString("hisoldvalue"));
        history.setNewValue(rs.getString("hisnewvalue"));
        history.setProcessDate(rs.getTimestamp("hisprocessdate"));
        history.setfNewValue(rs.getString("hisfk_newvalue"));
        history.setfOldValue(rs.getString("hisfk_oldvalue"));
        history.setReferencetable(rs.getString("hisfk_tablename"));
        history.setTableName(rs.getString("histablename"));
        history.setColumnType(rs.getString("hiscolumntype"));
        history.setItemValue(rs.getString("hisitemvalue"));
        history.getBranch().setName(rs.getString("branchname"));

        return history;

    }

}

/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   21.10.2016 16:00:46
 */
package com.mepsan.marwiz.general.pcrdmymenu.dao;

import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserDataConnectionMapper implements RowMapper<UserDataMenuConnection> {

    @Override
    public UserDataMenuConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDataMenuConnection userDataMenuConnection = new UserDataMenuConnection();

        Page page = new Page(rs.getInt("page_id"), rs.getString("url"));
        page.setName(rs.getString("name"));

        userDataMenuConnection.setPage(page);
        userDataMenuConnection.setColor(rs.getString("color"));
        userDataMenuConnection.setIcon(rs.getString("icon"));
        userDataMenuConnection.setOrder(rs.getInt("order"));
        userDataMenuConnection.setId(rs.getInt("id"));

        return userDataMenuConnection;
    }

}

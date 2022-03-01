/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 10:54:52
 */

package com.mepsan.marwiz.system.userdata.dao;

import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class UserDataAuthorizeConnectionMapper implements RowMapper<UserDataAuthorizeConnection>{

    @Override
    public UserDataAuthorizeConnection mapRow(ResultSet rs, int i) throws SQLException {
        UserDataAuthorizeConnection userDataAuthorizeConnection=new UserDataAuthorizeConnection();
        userDataAuthorizeConnection.setId(rs.getInt("autcid"));
        userDataAuthorizeConnection.getAuthorize().setId(rs.getInt("autcauthorize_id"));
        userDataAuthorizeConnection.getAuthorize().setName(rs.getString("authname"));
        userDataAuthorizeConnection.getAuthorize().getBranch().setId(rs.getInt("bid"));
        userDataAuthorizeConnection.getAuthorize().getBranch().setName(rs.getString("bname"));
        userDataAuthorizeConnection.getUserData().setId(rs.getInt("autcuserdata_id"));
        userDataAuthorizeConnection.getUserData().setName(rs.getString("usname"));
        userDataAuthorizeConnection.getUserData().setSurname(rs.getString("ussurname"));
        return userDataAuthorizeConnection;
    }

}

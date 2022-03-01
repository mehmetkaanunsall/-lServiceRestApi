/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 01:13:16
 */
package com.mepsan.marwiz.system.authorize.dao;

import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

public class AuthorizeMapper implements RowMapper<Authorize> {

    @Override
    public Authorize mapRow(ResultSet rs, int i) throws SQLException {
        Authorize authorize = new Authorize();
        UserData userData = new UserData();
        authorize.setId(rs.getInt("authid"));
        authorize.setName(rs.getString("authname"));
        try {
            authorize.setListOfModules(convertToArray(rs.getString("authmodules")));
            authorize.setListOfFolders(convertToArray(rs.getString("authfolders")));
            authorize.setListOfPages(convertToArray(rs.getString("authpages")));
            authorize.setListOfTabs(convertToArray(rs.getString("authtabs")));
            authorize.setListOfButtons(convertToArray(rs.getString("authbuttons")));
            authorize.setIsAdmin(rs.getBoolean("authis_admin"));
        } catch (Exception ex) {
        }
        try {
            userData.setId(rs.getInt("authc_id"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            authorize.setUserCreated(userData);
            authorize.setDateCreated(rs.getTimestamp("authc_time"));
        } catch (Exception e) {

        }
        return authorize;
    }

    public List<Integer> convertToArray(String s) {
        List<Integer> array = new ArrayList<>();
        if (s != null) {
            String[] t = s.split(",");
            for (String a : t) {
                array.add(Integer.valueOf(a));
            }
        }
        return array;
    }

}

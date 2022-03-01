/**
 * Bu sınıf, USerData nesnesini oluşturur ve özelliklerini set eder.
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 12.01.2018 08:45
 */
package com.mepsan.marwiz.system.userdata.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserDataMapper implements RowMapper<UserData> {

    @Override
    public UserData mapRow(ResultSet rs, int i) throws SQLException {

        UserData userdata = new UserData();

        userdata.setId(rs.getInt("usid"));
        userdata.setName(rs.getString("usname"));
        userdata.setSurname(rs.getString("ussurname"));

        try {
            userdata.setUsername(rs.getString("ususername"));
            userdata.setPhone(rs.getString("usphone"));
            userdata.setMail(rs.getString("usmail"));
            userdata.setAddress(rs.getString("usaddress"));
            userdata.getCounty().setId(rs.getInt("uscounty_id"));
            userdata.getCity().setId(rs.getInt("uscity_id"));
            userdata.getCountry().setId(rs.getInt("uscountry_id"));
            userdata.setIsRightNumeric(rs.getBoolean("usis_rightnumeric"));
            userdata.getAccount().setId(rs.getInt("usaccount_id"));
            userdata.getAccount().setName(rs.getString("accname"));
            userdata.getAccount().setTitle(rs.getString("acctitle"));
            userdata.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));

            UserData createUserData = new UserData();
            userdata.setUserCreated(createUserData);
            userdata.getUserCreated().setUsername(rs.getString("us2username"));
            userdata.getUserCreated().setName(rs.getString("us2name"));
            userdata.getUserCreated().setSurname(rs.getString("us2surname"));

            userdata.getLastBranch().setId(rs.getInt("bid"));
            userdata.getLastBranch().setName("bname");
            userdata.getLastAuthorize().setId(rs.getInt("uslastauthorize_id"));
            userdata.getLastAuthorize().setName(rs.getString("authname"));
            userdata.getType().setId(rs.getInt("ustype_id"));
            userdata.getType().setTag(rs.getString("typdname"));
            userdata.getLanguage().setId(rs.getInt("uslanguage_id"));
            userdata.getLanguage().setTag(rs.getString("lngdname"));
            userdata.getStatus().setId(rs.getInt("usstatus_id"));
            userdata.getStatus().setTag(rs.getString("stcdname"));
            userdata.setIsAuthorized(rs.getBoolean("usis_authorized"));
            userdata.setIsCashierAddSalesBasket(rs.getBoolean("usis_cashieraddsalesbasket"));

        } catch (Exception e) {

        }
        try {
            userdata.setMposPages(rs.getString("usmpospages"));
            userdata.setDateCreated(rs.getTimestamp("usc_time"));
        } catch (Exception e) {

        }
        try {
            userdata.setIsAdmin(rs.getBoolean("isadmin"));//
        } catch (Exception e) {
        }

        return userdata;
    }
}

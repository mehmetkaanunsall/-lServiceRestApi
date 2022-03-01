/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 02:00:52
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int i) throws SQLException {
        Account account = new Account();
        UserData userData = new UserData();
        account.setId(rs.getInt("accid"));
        account.setName(rs.getString("accname"));

        try {
            account.setOnlyAccountName(rs.getString("accname"));
            account.setTitle(rs.getString("acctitle"));
        } catch (Exception e) {
        }

        try {
            account.setIsPerson(rs.getBoolean("accis_person"));
            account.setCode(rs.getString("acccode"));
            account.setTaxNo(rs.getString("acctaxno"));
            account.setTaxOffice(rs.getString("acctaxoffice"));
            account.getStatus().setId(rs.getInt("accstatus_id"));
            account.getStatus().setTag(rs.getString("sttdname"));
            account.getType().setId(rs.getInt("acctype_id"));
            account.getType().setTag(rs.getString("typdname"));
            account.setBalance(rs.getBigDecimal("accbalance"));
            account.setCreditlimit(rs.getBigDecimal("acccreditlimit"));
            account.setPhone(rs.getString("accphone"));
            account.setEmail(rs.getString("accemail"));
            account.setAddress(rs.getString("accaddress"));
            account.getCity().setId(rs.getInt("acccity_id"));
            account.getCity().setTag("ctydname");
            account.getCountry().setId(rs.getInt("acccountry_id"));
            account.getCountry().setTag(rs.getString("ctrdname"));
            account.getCounty().setId(rs.getInt("acccounty_id"));
            account.getCounty().setName(rs.getString("cntyname"));
            account.setIsEmployee(rs.getBoolean("accisemployee"));
        } catch (Exception e) {

        }

        try {
            account.setCenteraccount_id(rs.getInt("acccenteraccount_id"));

        } catch (Exception e) {
        }
        try {
            account.setDescription(rs.getString("accdescription"));
        } catch (Exception e) {
        }

        try {
            account.setTaxpayertype_id(rs.getInt("acctaxpayertype_id"));
            account.setTagInfo(rs.getString("acctaginfo"));

        } catch (Exception e) {
        }
        try {
            account.setMaxExpiryCount(rs.getInt("accmaxexpriycount"));
        } catch (Exception e) {
        }
        try {
            userData.setId(rs.getInt("accc_id"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            account.setUserCreated(userData);
            account.setDateCreated(rs.getTimestamp("accc_time"));
        } catch (Exception e) {

        }
        try {
            if (rs.getString("accdueday") == null) {
                account.setDueDay(null);
            } else {
                account.setDueDay(rs.getInt("accdueday"));
            }

        } catch (Exception e) {
        }
        try {
            account.setPaymenttype_id(rs.getInt("accpaymenttye_id"));
        } catch (Exception e) {
        }
        try {
            account.setTagQuantity(rs.getInt("tagquantity"));
        } catch (Exception e) {
        }
        return account;
    }

}

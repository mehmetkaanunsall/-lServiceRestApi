/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samet.dag
 */
public class AccountDetailMapper implements RowMapper<AccountInfo> {

    @Override
    public AccountInfo mapRow(ResultSet rs, int i) throws SQLException {
        AccountInfo accountInfo = new AccountInfo();

        accountInfo.setId(rs.getInt("aiid"));
        accountInfo.setFuelintegrationcode(rs.getString("aifuelintegrationcode"));
        accountInfo.setAccountingintegrationcode(rs.getString("aiaccountingintegrationcode"));

        return accountInfo;
    }

}

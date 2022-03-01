/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class AccountBranchMapper implements RowMapper<AccountBranchCon>{

    @Override
    public AccountBranchCon mapRow(ResultSet rs, int i) throws SQLException {
        AccountBranchCon accountBranchCon = new AccountBranchCon();
        accountBranchCon.setId(rs.getInt("abcid"));
        accountBranchCon.getBranch().setId(rs.getInt("abcbranch_id"));
        accountBranchCon.getBranch().setName(rs.getString("brname"));
        accountBranchCon.setBalance(rs.getBigDecimal("abcbalance"));
        return accountBranchCon;
    }
    
}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.11.2019 08:41:20
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountBank;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountBankMapper implements RowMapper<AccountBank> {

    @Override
    public AccountBank mapRow(ResultSet rs, int i) throws SQLException {
        AccountBank accountBank = new AccountBank();

        accountBank.setId(rs.getInt("accbid"));
        accountBank.setName(rs.getString("accbname"));
        accountBank.setAccountNumber(rs.getString("accbaccountnumber"));
        accountBank.setIbanNumber(rs.getString("accbibannumber"));
        accountBank.getCurrency().setId(rs.getInt("accbcurrency_id"));
        accountBank.getCurrency().setTag(rs.getString("crrdname"));
        accountBank.getType().setId(rs.getInt("accbtype_id"));
        accountBank.getType().setTag(rs.getString("typdname"));
        accountBank.getStatus().setId(rs.getInt("accbstatus_id"));
        accountBank.getStatus().setTag(rs.getString("sttdname"));
        accountBank.setBalance(rs.getBigDecimal("accbbalance"));
        accountBank.getBankBranch().setCode(rs.getString("bkbcode"));
        accountBank.getBankBranch().getBank().setId(rs.getInt("bkbbank_id"));
        accountBank.getBankBranch().getBank().setName(rs.getString("bnkname"));
        accountBank.getBankBranch().setId(rs.getInt("accbbankbranch_id"));
        accountBank.getBankBranch().setName(rs.getString("bkbname"));
        return accountBank;
    }

}

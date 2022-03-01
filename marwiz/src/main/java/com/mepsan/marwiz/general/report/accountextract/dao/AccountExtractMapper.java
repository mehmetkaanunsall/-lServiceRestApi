/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 11:53:30
 */
package com.mepsan.marwiz.general.report.accountextract.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountExtractMapper implements RowMapper<AccountExtract> {

    @Override
    public AccountExtract mapRow(ResultSet rs, int i) throws SQLException {
        AccountExtract ae = new AccountExtract();
        ae.setId(rs.getInt("accid"));
        ae.setInComing(rs.getBigDecimal("credit"));//alacak
        ae.setOutComing(rs.getBigDecimal("dept"));//borç
        ae.setBalance(rs.getBigDecimal("balance"));
        try {
        ae.setIsPerson(rs.getBoolean("accis_person"));
        ae.setName(rs.getString("accname"));
        ae.setTitle(rs.getString("acctitle"));
        ae.setIsEmployee(rs.getBoolean("accisemployee"));
        ae.getBranch().setId(rs.getInt("brid"));
        ae.getBranch().setName(rs.getString("brname"));
        ae.setAccount_branch_con_id(rs.getInt("abcid"));
        } catch(Exception e)  {

        }
        return ae;
    }

}

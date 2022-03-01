/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:33:46
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankAccountBranchMapper implements RowMapper<BankAccountBranchCon> {

    @Override
    public BankAccountBranchCon mapRow(ResultSet rs, int i) throws SQLException {
        BankAccountBranchCon bankAccountBranchCon = new BankAccountBranchCon();
        bankAccountBranchCon.setId(rs.getInt("babcid"));
        bankAccountBranchCon.getBranch().setId(rs.getInt("babcbranch_id"));
        bankAccountBranchCon.getBranch().setName(rs.getString("brname"));
        bankAccountBranchCon.setBalance(rs.getBigDecimal("babcbalance"));
        bankAccountBranchCon.getBranch().setIsCentral(rs.getBoolean("bris_central"));

        return bankAccountBranchCon;
    }

}
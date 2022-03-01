/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:39:42
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.model.finance.BankBranch;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankBranchMapper implements RowMapper<BankBranch> {

    @Override
    public BankBranch mapRow(ResultSet rs, int i) throws SQLException {
        BankBranch bankBranch = new BankBranch();

        bankBranch.setId(rs.getInt("bkbid"));
        bankBranch.setName(rs.getString("bkbname"));
        bankBranch.setCode(rs.getString("bkbcode"));

        try {
            bankBranch.getBank().setName(rs.getString("bname"));
        } catch (Exception e) {
        }
        return bankBranch;
    }

}

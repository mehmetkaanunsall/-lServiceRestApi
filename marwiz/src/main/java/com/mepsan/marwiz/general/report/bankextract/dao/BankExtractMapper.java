/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 08.03.2018 15:38:09
 */
package com.mepsan.marwiz.general.report.bankextract.dao;

import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankExtractMapper implements RowMapper<BankAccountMovement> {

    @Override
    public BankAccountMovement mapRow(ResultSet rs, int i) throws SQLException {
        BankAccountMovement bam = new BankAccountMovement();

        bam.getBankAccount().setId(rs.getInt("baid"));
        bam.getBankAccount().setName(rs.getString("baname"));
        bam.getBankAccount().setBalance(rs.getBigDecimal("bbcbalance"));
        bam.getBankAccount().getCurrency().setId(rs.getInt("crrid"));
        bam.getBankAccount().getCurrency().setCode(rs.getString("crrcode"));

        bam.setTotalIncoming(rs.getBigDecimal("sumincoming"));

        bam.setTotalOutcoming(rs.getBigDecimal("sumoutcoming"));
        
        bam.getBranch().setId(rs.getInt("brid"));
        bam.getBranch().setName(rs.getString("brname"));
        bam.setId(rs.getInt("bbcid"));

        return bam;
    }

}
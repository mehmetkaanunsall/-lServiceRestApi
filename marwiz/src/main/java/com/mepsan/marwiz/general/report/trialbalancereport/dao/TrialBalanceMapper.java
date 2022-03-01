package com.mepsan.marwiz.general.report.trialbalancereport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samet.dag
 */
public class TrialBalanceMapper implements RowMapper<TrialBalance> {

    @Override
    public TrialBalance mapRow(ResultSet rs, int i) throws SQLException {

        TrialBalance trialBalance = new TrialBalance();

        trialBalance.setAccountName(rs.getString("accname"));

        trialBalance.setIncome(rs.getBigDecimal("sumincoming"));
        trialBalance.setExpense(rs.getBigDecimal("sumoutcoming"));
        if (rs.getString("accname").equals("2")) {
            trialBalance.setIncome(rs.getBigDecimal("sumoutcoming"));
            trialBalance.setExpense(rs.getBigDecimal("sumincoming"));
        }

        try {
            trialBalance.setName(rs.getString("name"));
        } catch (SQLException e) {

        }
        try {
            trialBalance.setBranchName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        return trialBalance;
    }

}

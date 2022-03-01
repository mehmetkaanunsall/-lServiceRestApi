/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.dao;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseMapper implements RowMapper<IncomeExpense> {

    @Override
    public IncomeExpense mapRow(ResultSet rs, int i) throws SQLException {
        IncomeExpense incomeExpense = new IncomeExpense();
        incomeExpense.setId(rs.getInt("fieid"));
        incomeExpense.setIsIncome(rs.getBoolean("fieis_income"));

        incomeExpense.setName(rs.getString("fiename"));

        try {
            IncomeExpense parentId = new IncomeExpense();
            parentId.setId(rs.getInt("fieparent_id"));
            incomeExpense.setParentId(parentId);
        } catch (Exception e) {
        }
        try {
            incomeExpense.setIsProfitMarginReport(rs.getBoolean("fieis_profitmarginreport"));
        } catch (Exception e) {
        }
        try {
            incomeExpense.setBalance(rs.getBigDecimal("fiebalance"));
        } catch (Exception e) {
        }

        try {
            incomeExpense.setTotalPrice(rs.getBigDecimal("fiemprice"));
            incomeExpense.setTotalExchagePrice(rs.getBigDecimal("fiemexchangeprice"));
            incomeExpense.getParentId().setName(rs.getString("fie1name"));
        } catch (Exception e) {
        }

        return incomeExpense;
    }

}

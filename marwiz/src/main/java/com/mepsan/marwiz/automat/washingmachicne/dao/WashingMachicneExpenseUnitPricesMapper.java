/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:35:22 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicneExpenseUnitPricesMapper implements RowMapper<ExpenseUnitPrice> {

    @Override
    public ExpenseUnitPrice mapRow(ResultSet rs, int i) throws SQLException {
        ExpenseUnitPrice expenseUnitPrice = new ExpenseUnitPrice();

        expenseUnitPrice.setId(rs.getInt("eupid"));
        expenseUnitPrice.getStock().setId(rs.getInt("eupstock_id"));
        expenseUnitPrice.getStock().setName(rs.getString("stckname"));
        expenseUnitPrice.setUnitPrice(rs.getBigDecimal("eupunitprice"));
        expenseUnitPrice.getCurrency().setId(rs.getInt("eupcurrency_id"));
        expenseUnitPrice.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        expenseUnitPrice.getStock().getUnit().setSortName(rs.getString("untsortname"));

        return expenseUnitPrice;
    }

}

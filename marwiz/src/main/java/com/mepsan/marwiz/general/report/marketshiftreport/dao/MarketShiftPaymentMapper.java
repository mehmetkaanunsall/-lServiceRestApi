/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.10.2018 09:18:41
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftPaymentMapper implements RowMapper<MarketShiftPayment> {

    @Override
    public MarketShiftPayment mapRow(ResultSet rs, int i) throws SQLException {
        MarketShiftPayment marketShiftPayment = new MarketShiftPayment();
        marketShiftPayment.setId(rs.getInt("shpid"));
        marketShiftPayment.getAccount().setId(rs.getInt("shpaccount_id"));
        marketShiftPayment.getAccount().setId(rs.getInt("accid"));
        marketShiftPayment.getAccount().setName(rs.getString("accname"));
        marketShiftPayment.getAccount().setTitle(rs.getString("acctitle"));
        marketShiftPayment.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        marketShiftPayment.setActualSalesPrice(rs.getBigDecimal("shpaccualprice"));
        marketShiftPayment.getBankAccount().setId(rs.getInt("shpbankaccount_id"));
        marketShiftPayment.getBankAccount().setName(rs.getString("baname"));
        marketShiftPayment.getBankAccount().getType().setId(rs.getInt("batype_id"));
        marketShiftPayment.getCurrency().setId(rs.getInt("shpcurrency_id"));
        marketShiftPayment.setIs_check(rs.getBoolean("shpis_check"));
        marketShiftPayment.getSafe().setId(rs.getInt("shpsafe_id"));
        marketShiftPayment.getSafe().setName(rs.getString("sfname"));
        marketShiftPayment.setSalesPrice(rs.getBigDecimal("shpsaleprice"));
        marketShiftPayment.getSaleType().setId(rs.getInt("shpsaletype_id"));
        marketShiftPayment.getSaleType().setTag(rs.getString("typdname"));
        marketShiftPayment.getShift().setId(rs.getInt("shpshift_id"));

        try {
            marketShiftPayment.setIsAvaialbelFinancingDocument(rs.getBoolean("isAvailableFinancingDoc"));
        } catch (Exception e) {
        }
        try {
            marketShiftPayment.setInheritedMoney(rs.getBigDecimal("shpinheritedmoney"));
        } catch (Exception e) {
        }
        return marketShiftPayment;
    }

}

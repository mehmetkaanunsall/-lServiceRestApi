/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.02.2018 02:38:48
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.SalePayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftSalePaymentMapper implements RowMapper<SalePayment> {
    
    @Override
    public SalePayment mapRow(ResultSet rs, int i) throws SQLException {
        SalePayment salePayment = new SalePayment();
        try {
            salePayment.setId(rs.getInt("slpid"));
        } catch (Exception e) {
        }
        try {
            salePayment.getUser().setId(rs.getInt("sluserdata_id"));
            salePayment.getUser().setName(rs.getString("usname"));
            salePayment.getUser().setSurname(rs.getString("ussurname"));
        } catch (Exception e) {
        }
        try {
            salePayment.getType().setId(rs.getInt("slptype_id"));
            salePayment.getType().setTag(rs.getString("typdname"));
            salePayment.setPrice(rs.getBigDecimal("slpprice"));
            salePayment.getCurrency().setId(rs.getInt("slpcurrency_id"));
        } catch (Exception e) {
        }
        try {
            salePayment.setSaleCount(rs.getInt("countsale"));
        } catch (Exception e) {
        }
        
        return salePayment;
    }
    
}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.03.2018 05:30:53
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import com.mepsan.marwiz.general.model.general.SalePayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesTypeReportMapper implements RowMapper<SalePayment> {

    @Override
    public SalePayment mapRow(ResultSet rs, int i) throws SQLException {
        SalePayment salePayment = new SalePayment();

        salePayment.getType().setId(rs.getInt("slptype_id"));
        salePayment.getType().setTag(rs.getString("typdname"));
        salePayment.setPrice(rs.getBigDecimal("slpprice"));
        salePayment.getCurrency().setId(rs.getInt("slpcurrency_id"));
        return salePayment;
    }

}

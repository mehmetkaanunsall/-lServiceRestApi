/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.10.2019 05:20:22
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.SalePayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftReportSalePaymentMapper implements RowMapper<SalePayment> {

    @Override
    public SalePayment mapRow(ResultSet rs, int i) throws SQLException {
        SalePayment salePayment = new SalePayment();
        salePayment.getSales().getCurrency().setId(rs.getInt("slcurrency_id"));
        salePayment.setPrice(rs.getBigDecimal("slpprice"));
        salePayment.getType().setId(rs.getInt("slptype_id"));
        salePayment.getType().setTag(rs.getString("typdname"));
        salePayment.setId(rs.getInt("slid"));

        return salePayment;
    }

}
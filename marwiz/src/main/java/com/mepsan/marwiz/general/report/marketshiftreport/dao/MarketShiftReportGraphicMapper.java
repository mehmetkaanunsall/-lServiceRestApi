/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.02.2018 11:55:11
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.Sales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftReportGraphicMapper implements RowMapper<Sales> {

    @Override
    public Sales mapRow(ResultSet rs, int i) throws SQLException {
        Sales sale = new Sales();
        try {
            sale.getPointOfSale().setId(rs.getInt("posid"));
            sale.getPointOfSale().setName(rs.getString("posname"));

        } catch (Exception e) {
        }
        try {
            sale.getUser().setId(rs.getInt("usid"));
            sale.getUser().setName(rs.getString("usname"));
            sale.getUser().setSurname(rs.getString("ussurname"));
        } catch (Exception e) {
        }

        sale.setTotalPrice(rs.getBigDecimal("totalprice"));
        sale.getCurrency().setId(rs.getInt("slcurrency_id"));
        sale.setTotalMoney(rs.getBigDecimal("totalmoney"));

        return sale;
    }

}

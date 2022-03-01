/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:58:40
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatShiftReportDetailMapper implements RowMapper<AutomatSales> {

    @Override
    public AutomatSales mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSales automatSales = new AutomatSales();
        automatSales.setId(rs.getInt("aslid"));
        automatSales.getCurrency().setId(rs.getInt("aslcurrency_id"));
        automatSales.setTotalMoney(rs.getBigDecimal("asltotalmoney"));
        try {

            automatSales.setSaleDateTime(rs.getTimestamp("aslsaledatetime"));
            automatSales.setTotalPrice(rs.getBigDecimal("asltotalprice"));
            automatSales.setDiscountPrice(rs.getBigDecimal("asltotaldiscount"));
            automatSales.setTaxPrice(rs.getBigDecimal("asltotaltax"));
            automatSales.getPlatform().setId(rs.getInt("aslplatform_id"));
            automatSales.setPlatformNo(rs.getString("aslplatformno"));
            automatSales.getWashingMachine().setId(rs.getInt("aslwashingmachine_id"));
            automatSales.getWashingMachine().setName(rs.getString("wshname"));
            automatSales.setPaymentType(rs.getInt("aslpaymenttype_id"));
        } catch (Exception e) {
        }

        try {
            automatSales.setMacAddress(rs.getString("aslmacaddress"));
        } catch (Exception e) {
        }

        return automatSales;
    }

}

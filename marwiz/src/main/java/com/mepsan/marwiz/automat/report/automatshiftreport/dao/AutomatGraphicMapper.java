/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:51:45 AM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatGraphicMapper implements RowMapper<AutomatSales> {

    @Override
    public AutomatSales mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSales item = new AutomatSales();

        try { // ürün bazında satışları listeler
            item.getStock().setId(rs.getInt("stckid"));
            item.getStock().setName(rs.getString("stckname"));
            item.setOperationAmount(rs.getBigDecimal("liter"));
            item.setTotalMoney(rs.getBigDecimal("totalmoney"));

        } catch (Exception e) {
        }

        try { // platform satışları
            item.setPlatformNo(rs.getString("slplatformno"));
            item.setOperationAmount(rs.getBigDecimal("liter"));
            item.setTotalMoney(rs.getBigDecimal("totalmoney"));

        } catch (Exception e) {
        }

        try { // ödeme tipleri bazında satışları listeler
            item.setPaymentType(rs.getInt("ptid"));
            item.setOperationAmount(rs.getBigDecimal("liter"));
            item.setTotalMoney(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }

        try {
            item.getCurrency().setId(rs.getInt("slcurrency_id"));
            item.getCurrency().setTag(rs.getString("crydname"));
        } catch (Exception e) {
        }

        item.getStock().getUnit().setId(rs.getInt("sliunit_id"));
        item.getStock().getUnit().setSortName(rs.getString("untsortname"));
        item.getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));

        try {
            item.getWashingMachine().setName(rs.getString("wshname"));
        } catch (Exception e) {
        }
        return item;
    }
}

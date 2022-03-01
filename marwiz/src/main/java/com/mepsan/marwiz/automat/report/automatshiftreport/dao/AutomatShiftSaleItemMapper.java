/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2019 08:43:00
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatShiftSaleItemMapper implements RowMapper<AutomatSales> {

    @Override
    public AutomatSales mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSales automatSalesItem = new AutomatSales();
        automatSalesItem.setId(rs.getInt("asliid"));
        automatSalesItem.setSaleDateTime(rs.getTimestamp("aslisaledatetime"));
        automatSalesItem.getStock().getUnit().setId(rs.getInt("asliunit_id"));
        automatSalesItem.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        automatSalesItem.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        automatSalesItem.setOperationAmount(rs.getBigDecimal("asliquantity"));
        automatSalesItem.setUnitPrice(rs.getBigDecimal("asliunitprice"));
        automatSalesItem.setTotalPrice(rs.getBigDecimal("aslitotalprice"));
        automatSalesItem.setTotalMoney(rs.getBigDecimal("aslitotalmoney"));
        automatSalesItem.getStock().setId(rs.getInt("aslistock_id"));
        automatSalesItem.getStock().setName(rs.getString("stckname"));
        automatSalesItem.getStock().setCode(rs.getString("stckcode"));
        automatSalesItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        automatSalesItem.getStock().setBarcode(rs.getString("stckbarcode"));
        automatSalesItem.getTank().setId(rs.getInt("aslitank_id"));
        automatSalesItem.setTankNo(rs.getString("aslitankno"));
        automatSalesItem.getNozzle().setId(rs.getInt("aslinozzle_id"));
        automatSalesItem.setNozzleNo(rs.getString("aslinozzleno"));
        return automatSalesItem;
    }

}

/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 05.02.2019 18:22:42
 */
package com.mepsan.marwiz.automation.tank.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TankMovementTabMapper implements RowMapper<TankMovement> {

    @Override
    public TankMovement mapRow(ResultSet rs, int i) throws SQLException {
        TankMovement tm = new TankMovement();
        tm.setId(rs.getInt("wmid"));
        tm.setMoveDate(rs.getTimestamp("movedate"));
        tm.setIsDirection(rs.getBoolean("wmis_direction"));
        tm.setQuantity(rs.getBigDecimal("wmquantity"));
        tm.getStock().setId(rs.getInt("wmstock_id"));
        tm.getStock().setName(rs.getString("stckname"));
        tm.getStock().setCode(rs.getString("stckcode"));
        tm.getStock().setBarcode(rs.getString("stckbarcode"));
        tm.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        tm.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        tm.getStock().getUnit().setName(rs.getString("guntname"));
        tm.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        tm.getWarehouse().setId(rs.getInt("wmwarehouse_id"));
        tm.getWarehouse().setName(rs.getString("whname"));
        tm.getWarehouse().setCode(rs.getString("whcode"));
        tm.getWaybill().setId(rs.getInt("waybillid"));
        tm.getWarehouseReceipt().setId(rs.getInt("wmwarehousereceipt_id"));
        tm.getWarehouseReceipt().setReceiptNumber(rs.getString("receiptnumber"));
        tm.setUnitPriceWithoutTax(rs.getBigDecimal("unitpricewithouttaxrate"));
        tm.setUnitPriceWithTax(rs.getBigDecimal("unitpricewithtaxrate"));
        tm.setRemainingAmount(rs.getBigDecimal("lastquantity"));
        tm.setTotalPrice(rs.getBigDecimal("totalprice"));
        tm.setTotalMoney(rs.getBigDecimal("totalmoney"));
        tm.setTaxRate(rs.getBigDecimal("taxrate"));
        tm.getUserCreated().setId(rs.getInt("wmc_id"));
        tm.getUserCreated().setName(rs.getString("usdname"));
        tm.getUserCreated().setSurname(rs.getString("usdsurname"));
        tm.setType(rs.getInt("type"));
        return tm;
    }

}

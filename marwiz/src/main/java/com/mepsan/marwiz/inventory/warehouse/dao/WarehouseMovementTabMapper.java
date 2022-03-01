package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Samet Dağ
 */
public class WarehouseMovementTabMapper implements RowMapper<StockMovement> {

    @Override
    public StockMovement mapRow(ResultSet rs, int i) throws SQLException {
        StockMovement sm = new StockMovement();

        sm.getStock().setName(rs.getString("stckname"));
        sm.setId(rs.getInt("wmid"));
        sm.setMoveDate(rs.getTimestamp("movedate"));
        sm.setIsDirection(rs.getBoolean("wmis_direction"));
        sm.setQuantity(rs.getBigDecimal("wmquantity"));
        sm.getWarehouse().setId(rs.getInt("whid"));
        sm.getWarehouse().setName(rs.getString("whname"));
        sm.setRemainingAmount(rs.getDouble("lastquantity"));
        sm.getStock().setId(rs.getInt("wmstock_id"));
        sm.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        sm.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        sm.getStock().getUnit().setName(rs.getString("guntname"));
        sm.getStock().getUnit().setSortName(rs.getString("guntsortname"));

        return sm;
    }

}

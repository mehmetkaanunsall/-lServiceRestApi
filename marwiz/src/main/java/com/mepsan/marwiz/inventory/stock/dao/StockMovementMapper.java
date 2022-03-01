/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:53:31
 */
package com.mepsan.marwiz.inventory.stock.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockMovementMapper implements RowMapper<StockMovement> {

    @Override
    public StockMovement mapRow(ResultSet rs, int i) throws SQLException {
        StockMovement sm = new StockMovement();
        
        try {
            sm.setId(rs.getInt("wmid"));
        } catch (Exception e) {
        }
        try {
            sm.getWarehouse().setName(rs.getString("whname"));
            sm.getWarehouse().setId(rs.getInt("whid"));
        } catch (Exception e) {
        }

        try {

            sm.setMoveDate(rs.getTimestamp("movedate"));

            sm.setIsDirection(rs.getBoolean("wmis_direction"));
            sm.setQuantity(rs.getBigDecimal("wmquantity"));
            sm.setRemainingAmount(rs.getDouble("lastquantity"));
            sm.getWarehouseReceipt().setReceiptNumber(rs.getString("wrreceiptnumber"));
            sm.getInvoice().setId(rs.getInt("invid"));
            sm.setProcessType(rs.getInt("processtype"));
            sm.setUnitPrice(rs.getBigDecimal("price"));
            sm.getStockTaking().setId(rs.getInt("stktid"));

            
        } catch (Exception e) {
        }

        try {
            sm.setQuantity(rs.getBigDecimal("whiquantity"));
        } catch (Exception e) {
        }
        try {
            sm.getBranch().setId(rs.getInt("brid"));
            sm.getBranch().setName(rs.getString("brname"));
        } catch (Exception e) {
        }
        try {
            sm.setTotalIncoming(rs.getBigDecimal("sumincoming"));
            sm.setTotalOutcoming(rs.getBigDecimal("sumoutcoming"));
            sm.setTransferAmount(rs.getBigDecimal("transferamount"));
        } catch (Exception e) {
        }

        return sm;
    }

}

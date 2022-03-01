/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 07:32:49
 */

package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class WarehouseReceiptMapper implements RowMapper<WarehouseReceipt>{

    @Override
    public WarehouseReceipt mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseReceipt warehouseReceipt=new WarehouseReceipt();
        warehouseReceipt.setId(rs.getInt("whrid"));
        warehouseReceipt.setReceiptNumber(rs.getString("whrreceiptnumber"));
        warehouseReceipt.setIsDirection(rs.getBoolean("whris_direction"));
        warehouseReceipt.setProcessDate(rs.getTimestamp("whrprocessdate"));
        warehouseReceipt.getType().setId(rs.getInt("whrtype_id"));
        warehouseReceipt.getType().setTag(rs.getString("typdname"));
        warehouseReceipt.getWarehouse().setId(rs.getInt("whrwarehouse_id"));
        warehouseReceipt.getWarehouse().setName(rs.getString("whname"));
         try {
            warehouseReceipt.setLogSapİsSend(rs.getBoolean("swhris_send"));
        } catch (Exception e) {
        }
        return warehouseReceipt;
    }

}

/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 02:12:55
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomationDeviceItemMapper implements RowMapper<AutomationDeviceItem> {

    @Override
    public AutomationDeviceItem mapRow(ResultSet rs, int i) throws SQLException {
        AutomationDeviceItem automationDeviceItem = new AutomationDeviceItem();
        automationDeviceItem.setId(rs.getInt("vmiid"));
        automationDeviceItem.setShelfNo(rs.getInt("vmishelfno"));
        automationDeviceItem.getStock().setId(rs.getInt("stckid"));
        automationDeviceItem.getStock().setName(rs.getString("stckname"));
        automationDeviceItem.getType().setId(rs.getInt("vmitype_id"));
        automationDeviceItem.setMaxStockLevel(rs.getBigDecimal("vmimaxstocklevel"));
        automationDeviceItem.getType().setTag(rs.getString("typdname"));
        automationDeviceItem.getStock().setBarcode(rs.getString("stckbarcode"));

        try {
            automationDeviceItem.setBalance(rs.getBigDecimal("vmibalance"));
            automationDeviceItem.setWarehouseAmount(rs.getBigDecimal("warehousequantity"));
        } catch (Exception e) {
        }

        return automationDeviceItem;

    }

}

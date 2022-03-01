/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2020 11:45:05
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomationDeviceMovementMapper implements RowMapper<AutomationDeviceItemMovement> {

    @Override
    public AutomationDeviceItemMovement mapRow(ResultSet rs, int i) throws SQLException {
        AutomationDeviceItemMovement automationDeviceItemMovement = new AutomationDeviceItemMovement();
        automationDeviceItemMovement.setId(rs.getInt("vmmid"));
        automationDeviceItemMovement.setProcessDate(rs.getTimestamp("vmmprocessdate"));
        automationDeviceItemMovement.setType(rs.getInt("vmmtype_id"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().setId(rs.getInt("vmmstock_id"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().setName(rs.getString("stckname"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().getUnit().setId(rs.getInt("guntid"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().getUnit().setName(rs.getString("guntname"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().getUnit().setSortName(rs.getString("guntsortname"));
        automationDeviceItemMovement.getAutomationDeviceItem().getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        automationDeviceItemMovement.getAutomationDeviceItem().setId(rs.getInt("vmmvendingmachineitem_id"));
        automationDeviceItemMovement.getAutomationDeviceItem().setShelfNo(rs.getInt("vmishelfno"));
        automationDeviceItemMovement.setQuantity(rs.getBigDecimal("vmmquantity"));
        automationDeviceItemMovement.setIsDirection(rs.getBoolean("vmmis_direction"));

        return automationDeviceItemMovement;
    }

}

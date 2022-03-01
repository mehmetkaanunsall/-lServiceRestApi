/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 12:40:34
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomationDeviceMapper implements RowMapper<AutomationDevice> {

    @Override
    public AutomationDevice mapRow(ResultSet rs, int i) throws SQLException {
        AutomationDevice automationDevice = new AutomationDevice();
        automationDevice.setId(rs.getInt("vmid"));
        automationDevice.setName(rs.getString("vmname"));
        automationDevice.setIpadress(rs.getString("vmipadress"));
        automationDevice.setPort(rs.getInt("vmport"));
        automationDevice.setDescription(rs.getString("vmdescription"));
        automationDevice.setMacAddress(rs.getString("vmmacaddress"));
        automationDevice.getWarehouse().setId(rs.getInt("vmwarehouse_id"));
        automationDevice.getBrand().setId(rs.getInt("vmbrand_id"));
        automationDevice.getProtocol().setId(rs.getInt("vmprotocol_id"));
        automationDevice.getProtocol().setProtocolNo(rs.getInt("prtprotocolno"));
        automationDevice.getDeviceType().setId(rs.getInt("vmtype_id"));
        automationDevice.getBrand().setName(rs.getString("brdname"));
        automationDevice.getProtocol().setName(rs.getString("prtname"));
        automationDevice.getDeviceType().setTag(rs.getString("typdname"));
        return automationDevice;
    }

}
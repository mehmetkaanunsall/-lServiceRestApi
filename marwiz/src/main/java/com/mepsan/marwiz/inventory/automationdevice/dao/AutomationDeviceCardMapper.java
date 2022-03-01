/**
 * 
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:05:19 PM
 */

package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class AutomationDeviceCardMapper implements  RowMapper<AutomationDeviceCard>{

    @Override
    public AutomationDeviceCard mapRow(ResultSet rs, int i) throws SQLException {
        
        AutomationDeviceCard automationDeviceCard=new AutomationDeviceCard();

        
        automationDeviceCard.setId(rs.getInt("ivmid"));
        automationDeviceCard.setRfNo(rs.getString("ivmrfno"));
        automationDeviceCard.getStatus().setId(rs.getInt("ivmstatus_id"));
        automationDeviceCard.getStatus().setTag(rs.getString("sttdname"));
        automationDeviceCard.setName(rs.getString("ivmname"));  
        automationDeviceCard.getType().setId(rs.getInt("ivmtype_id"));
        automationDeviceCard.getType().setTag(rs.getString("typdname"));
        
        return automationDeviceCard;
        
    }

}
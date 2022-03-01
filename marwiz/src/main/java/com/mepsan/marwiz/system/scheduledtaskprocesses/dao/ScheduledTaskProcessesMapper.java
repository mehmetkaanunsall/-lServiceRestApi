/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.dao;

import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class ScheduledTaskProcessesMapper implements RowMapper<ScheduledTaskProcesses> {
    
    @Override
    public ScheduledTaskProcesses mapRow(ResultSet rs, int i) throws SQLException {
        ScheduledTaskProcesses scheduledTask = new ScheduledTaskProcesses();
        
        scheduledTask.setId(rs.getInt("schid"));
        scheduledTask.getBranch().setId(rs.getInt("schbranch_id"));
        scheduledTask.getType().setId(rs.getInt("schtype_id"));
        scheduledTask.getType().setTag(rs.getString("typdname"));
        scheduledTask.setName(rs.getString("schname"));
        scheduledTask.getStatus().setId(rs.getInt("schstatus_id"));
        scheduledTask.getStatus().setTag(rs.getString("sttdname"));
        scheduledTask.setDescription(rs.getString("schdescription"));
        scheduledTask.setDays(rs.getString("schdays"));
        scheduledTask.setDaysDate(rs.getString("schdaysdate"));
        
        return scheduledTask;
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samet.dag
 */
public class PersonelDetailMapper implements RowMapper<EmployeeInfo> {
    
    @Override
    public EmployeeInfo mapRow(ResultSet rs, int i) throws SQLException {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        
        employeeInfo.setId(rs.getInt("eiid"));
        employeeInfo.setIntegrationcode(rs.getString("eiintegrationcode"));
        employeeInfo.setExactsalary(rs.getBigDecimal("eiexactsalary"));
        employeeInfo.setAgi(rs.getInt("eiagi"));
        employeeInfo.setStartdate(rs.getDate("eistartdate"));
        employeeInfo.setEnddate(rs.getDate("eienddate"));
        
        return employeeInfo;
    }
    
}

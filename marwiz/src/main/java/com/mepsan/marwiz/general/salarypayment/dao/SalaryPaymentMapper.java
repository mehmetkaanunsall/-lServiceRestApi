/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.salarypayment.dao;

import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samet.dag
 */
public class SalaryPaymentMapper implements RowMapper<EmployeeInfo> {
    
    @Override
    public EmployeeInfo mapRow(ResultSet rs, int i) throws SQLException {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setId(rs.getInt("eiid"));
        employeeInfo.getAccount().setName(rs.getString("accname"));
        employeeInfo.setExactsalary(rs.getBigDecimal("eiexactsalary"));
        employeeInfo.getAccountMovement().setBalance(rs.getBigDecimal("amvdebt"));
        employeeInfo.getAccountMovement().setPrice(rs.getBigDecimal("eisalarytobepaid"));
        employeeInfo.setAgi(rs.getInt("eiagi"));
        employeeInfo.getAccount().setId(rs.getInt("accid"));
        return employeeInfo;
    }
    
}

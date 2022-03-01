/**
 * This class ...
 *
 *           
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:52:51      
 */
package com.mepsan.marwiz.inventory.taxdepartment.dao;

import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TaxDepartmentMapper implements RowMapper<TaxDepartment> {

    @Override
    public TaxDepartment mapRow(ResultSet rs, int i) throws SQLException {
        TaxDepartment taxDepartment = new TaxDepartment();
        taxDepartment.setId(rs.getInt("txdid"));
        taxDepartment.setDepartmentNo(rs.getInt("txddepartmentno"));
        taxDepartment.setName(rs.getString("txdname"));
        taxDepartment.getTaxGroup().setId(rs.getInt("txdtaxgroup_id"));
        taxDepartment.getTaxGroup().setName(rs.getString("txgname"));

        try {
            UserData userData = new UserData();

            userData.setId(rs.getInt("usdid"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            taxDepartment.setUserCreated(userData);
            taxDepartment.setDateCreated(rs.getTimestamp("txgc_time"));
        } catch (Exception e) {

        }
        
        return taxDepartment;
        
    }

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class CheckDeleteMapper implements RowMapper<CheckDelete>{

    @Override
    public CheckDelete mapRow(ResultSet rs, int i) throws SQLException {
        
        CheckDelete checkDelete=new CheckDelete();
        checkDelete.setR_response(rs.getInt("r_response"));
        checkDelete.setR_recordno(rs.getString("r_recordno"));
        checkDelete.setR_record_id(rs.getInt("r_record_id"));
        
        return checkDelete;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ali.kurt
 */
public class TotalCountMapper implements RowMapper<TotalCount>{

    @Override
    public TotalCount mapRow(ResultSet rs, int i) throws SQLException {
        TotalCount tc = new TotalCount();
        
        tc.setDeletedCount(rs.getInt("deleted_count"));
        tc.setNotDeletedCount(rs.getInt("notdeleted_count"));
        tc.setSendCount(rs.getInt("send_count"));
        tc.setNotSendCount(rs.getInt("notsend_count"));
        
        return tc;
    }
    
}

/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:46:22 
 */

package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.finance.invoice.dao.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class RelatedRecordMapper implements RowMapper<RelatedRecord>{

    @Override
    public RelatedRecord mapRow(ResultSet rs, int i) throws SQLException {
        
        RelatedRecord rr =new RelatedRecord();
            
        rr.setId(rs.getInt("id"));
        rr.setDocumentNumber(rs.getString("documentnumber"));
        rr.setDocumentDate(rs.getTimestamp("processdate"));
        rr.setRelatedId(rs.getInt("rowid"));
        rr.setDocumentType(rs.getInt("type"));
        //rr.setIsRemoveButton(rs.getBoolean("is_removebutton"));
        return rr;
    }

}
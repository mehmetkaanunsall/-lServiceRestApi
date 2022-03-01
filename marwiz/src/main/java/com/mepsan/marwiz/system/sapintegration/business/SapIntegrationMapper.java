/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 15:20:44 
 */

package com.mepsan.marwiz.system.sapintegration.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class SapIntegrationMapper implements RowMapper<SapIntegration>{

    @Override
    public SapIntegration mapRow(ResultSet rs, int i) throws SQLException {
        
        SapIntegration si = new SapIntegration();
        
        si.getFinancingDocument().setId(rs.getInt("fodcid"));
        si.getFinancingDocument().setDocumentDate(rs.getTimestamp("fdocdocumentdate"));
        si.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
        si.getFinancingDocument().setDescription(rs.getString("fdocdesciption"));
        si.getFinancingDocument().setPrice(rs.getBigDecimal("fdocprice"));
        
        si.getBankAccount().setAccountNumber(rs.getString("baaccountnumber"));
        si.getBankAccount().setName(rs.getString("baname"));
        
        si.getSafe().setCode(rs.getString("sfcode"));
        
        si.setBranchCode(rs.getString("brserpintegrationcode"));
        si.setBranchId(rs.getInt("branch_id"));
        si.setIsSend(rs.getBoolean("sapis_send"));
        si.setResponse(rs.getString("sapresponse"));
        return si;
    }

}
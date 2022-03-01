/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class IncomingEInvoicesMapper implements RowMapper<IncomingEInvoice> {

    @Override
    public IncomingEInvoice mapRow(ResultSet rs, int i) throws SQLException {
        IncomingEInvoice ıei = new IncomingEInvoice();
        try {
            ıei.setId(rs.getInt("lgeiid"));
            ıei.setInvoiceId(rs.getInt("lgeiinvoice_id"));

            ıei.setProcessDate(rs.getTimestamp("lgeiprocessdate"));
            ıei.setIsSuccess(rs.getBoolean("lgeiis_success"));
            ıei.setResponseCode(rs.getString("lgeiresponsecode"));
            ıei.setResponseDescription(rs.getString("lgeiresponsedescription"));
            ıei.setApprovalStatusId(rs.getInt("lgeiapprovalstatus_id"));
        } catch (Exception e) {
        }

      
            try {
                ıei.setGetData(rs.getString("lgeigetdata"));
            } catch (Exception e) {
            }

       

  

        return ıei;
    }

}

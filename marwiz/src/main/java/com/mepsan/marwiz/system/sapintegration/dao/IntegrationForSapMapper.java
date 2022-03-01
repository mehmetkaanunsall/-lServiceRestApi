/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class IntegrationForSapMapper implements RowMapper<IntegrationForSap> {
    
    int type;
    
    public IntegrationForSapMapper(int type) {
        this.type = type;
    }
    
    @Override
    public IntegrationForSap mapRow(ResultSet rs, int i) throws SQLException {
          IntegrationForSap sap = new IntegrationForSap();
        try {
           
        sap.setId(rs.getInt("r_id"));
        sap.setJsonData(rs.getString("r_jsondata"));
        sap.setEvent(rs.getInt("r_event"));
        sap.setSendDate(rs.getTimestamp("r_senddate"));
        sap.setSendCount(rs.getInt("r_sendcount"));
        sap.setIsSend(rs.getBoolean("r_is_send"));
        sap.setTypeId(rs.getInt("r_type_id"));
        
        sap.setProcessDate(rs.getTimestamp("r_processdate"));
        } catch (Exception e) {
        }
       
        
        try {
            
            sap.setMessage(rs.getString("r_response"));

        } catch (Exception e) {
        }
        
        try {
               if (type == 1) {//Depo Fişleri
            sap.setType(rs.getString("r_type"));
            sap.setDocumentNumber(rs.getString("r_receiptnumber"));
            sap.setSapDocumentNumber(rs.getString("r_sapreceiptnumber"));
            sap.setIsDirection(rs.getBoolean("r_is_direction"));
            
        } else if (type == 3) {//Normal Satış Faturası

            sap.setDocumentNumber(rs.getString("r_documentnumber"));
            sap.setTotalMoney(rs.getBigDecimal("r_totalmoney"));
            sap.setDescription(rs.getString("r_description"));
            sap.setSapDocumentNumber(rs.getString("r_sapreceiptnumber"));
            
        } else {
            
            sap.setDocumentNumber(rs.getString("r_documentnumber"));
            sap.setTotalMoney(rs.getBigDecimal("r_totalmoney"));
            sap.setDescription(rs.getString("r_description"));
            sap.setSapDocumentNumber(rs.getString("r_invoicenumber"));
            
        }
        
        if (type == 2 || type == 3) {
            
            sap.setAccountName(rs.getString("r_accountname"));
        }
        } catch (Exception e) {
        }
        
     
        
        try {
            sap.setIsSendWaybill(rs.getBoolean("r_is_sendwaybill"));
        } catch (Exception e) {
        }
        
        try {
             sap.setSapIDocNo(rs.getString("r_sessionnumber"));
        sap.setObjectId(rs.getInt("r_object_id"));
        } catch (Exception e) {
        }
       
        
        return sap;
        
    }
    
}

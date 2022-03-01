/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class OrderMapper implements RowMapper<Order>{

    @Override
    public Order mapRow(ResultSet rs, int i) throws SQLException {
        Order order=new Order();
        order.setId(rs.getInt("odid"));
      
        order.getAccount().setName(rs.getString("accname"));
        order.getAccount().setTitle(rs.getString("acctitle"));
        order.getdNumber().setId(rs.getInt("oddocumentnumber_id"));
        order.setDocumentNumber(rs.getString("oddocumentnumber"));
        order.setOrderDate(rs.getTimestamp("odorderdate"));

        if (order.getdNumber().getId() > 0) {
            order.getdNumber().setActualNumber(rs.getInt("oddocumentnumber"));
            order.setDocumentSerial(rs.getString("oddocumentserial"));
        } else {
            order.setDocumentSerial(rs.getString("oddocumentserial"));
        }

        try {
            UserData userdata = new UserData();
            
            userdata.setId(rs.getInt("odc_id"));

            userdata.setName(rs.getString("usname"));
            userdata.setSurname(rs.getString("ussurname"));
            userdata.setUsername(rs.getString("ususername"));
            order.setUserCreated(userdata);
        } catch (Exception e) {
        }

        try {
            order.setDateCreated(rs.getTimestamp("odc_time"));
        } catch (Exception e) {
        }
        try {
            if (rs.getString("accdueday") == null) {
                order.getAccount().setDueDay(null);
            } else {
                order.getAccount().setDueDay(rs.getInt("accdueday"));
            }
        } catch (Exception e) {
        }

        try {

            order.getAccount().setId(rs.getInt("odaccount_id"));
            order.getAccount().setIsPerson(rs.getBoolean("accis_person"));
            order.getAccount().setPhone(rs.getString("accphone"));
            order.getAccount().setEmail(rs.getString("accemail"));

        } catch (Exception e) {
        }
        
        try {

            order.getStatus().setId(rs.getInt("odstatus_id"));
            order.getStatus().setTag(rs.getString("sttdname"));

        } catch (Exception e) {
        }
        
        try {

            order.getType().setId(rs.getInt("odtype_id"));
            order.setTypeNo(rs.getInt("odtypeno"));

        } catch (Exception e) {
        }

       
        try {
            order.getBranchSetting().getBranch().setId(rs.getInt("odbranch_id"));
            order.getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
            order.getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
            order.getBranchSetting().getBranch().setName(rs.getString("brname"));
            order.getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
            order.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
            order.getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
            
        } catch (Exception e) {
        }
        try {
            order.setRemainingQuantity(rs.getBigDecimal("odiremainingquantity"));
        } catch (Exception e) {
        }

        return order;
        
        

    }
    
}

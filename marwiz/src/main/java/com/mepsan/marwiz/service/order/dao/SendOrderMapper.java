/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.dao;

import com.mepsan.marwiz.general.model.log.SendOrder;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class SendOrderMapper implements RowMapper<SendOrder>{

    @Override
    public SendOrder mapRow(ResultSet rs, int i) throws SQLException {
        SendOrder sendOrder = new SendOrder();
        sendOrder.setId(rs.getInt("soid"));
        sendOrder.setOrderId(rs.getInt("soorderid"));
        sendOrder.setIssend(rs.getBoolean("soissend"));
        sendOrder.setSendbegindate(rs.getDate("sosendbegindate"));
        sendOrder.setSendenddate(rs.getDate("sosendenddate"));
        sendOrder.setSendcount(rs.getInt("sosendcount"));
        sendOrder.setSenddata(rs.getString("sosenddata"));
        sendOrder.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendOrder.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendOrder.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendOrder.setLicenceCode(rs.getString("solicencecode"));
        return sendOrder;
    }
    
}

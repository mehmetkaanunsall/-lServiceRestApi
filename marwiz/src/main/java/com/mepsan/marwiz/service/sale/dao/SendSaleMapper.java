/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 17:41:39 
 */
package com.mepsan.marwiz.service.sale.dao;

import com.mepsan.marwiz.general.model.log.SendSale;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendSaleMapper implements RowMapper<SendSale>{

    @Override
    public SendSale mapRow(ResultSet rs, int i) throws SQLException {
        SendSale sendSale = new SendSale();
        sendSale.setId(rs.getInt("ssid"));
        sendSale.setSaleId(rs.getInt("sssaleid"));
        sendSale.setIssend(rs.getBoolean("ssissend"));
        sendSale.setSendbegindate(rs.getDate("sssendbegindate"));
        sendSale.setSendenddate(rs.getDate("sssendenddate"));
        sendSale.setSendcount(rs.getInt("sssendcount"));
        sendSale.setSenddata(rs.getString("sssenddata"));
        sendSale.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendSale.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendSale.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendSale.setLicenceCode(rs.getString("sslicencecode"));
        return sendSale;
        
    }

}

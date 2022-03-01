/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 08:50:26
 */
package com.mepsan.marwiz.service.purchace.dao;

import com.mepsan.marwiz.general.model.log.SendPurchase;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendPurchaseMapper implements RowMapper<SendPurchase> {

    @Override
    public SendPurchase mapRow(ResultSet rs, int i) throws SQLException {
        SendPurchase sendPurchace = new SendPurchase();
        sendPurchace.setId(rs.getInt("spid"));
        sendPurchace.setInvoiceId(rs.getInt("spinvoiceid"));
        sendPurchace.setIssend(rs.getBoolean("spissend"));
        sendPurchace.setSendbegindate(rs.getDate("spsendbegindate"));
        sendPurchace.setSendenddate(rs.getDate("spsendenddate"));
        sendPurchace.setSendcount(rs.getInt("spsendcount"));
        sendPurchace.setSenddata(rs.getString("spsenddata"));
        sendPurchace.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendPurchace.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendPurchace.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendPurchace.setLicenceCode(rs.getString("splicencecode"));
        return sendPurchace;
    }

}

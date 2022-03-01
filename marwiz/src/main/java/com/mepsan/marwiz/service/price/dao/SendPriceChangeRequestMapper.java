/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 10:29:58
 */
package com.mepsan.marwiz.service.price.dao;

import com.mepsan.marwiz.general.model.log.SendPriceChangeRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendPriceChangeRequestMapper implements RowMapper<SendPriceChangeRequest> {

    @Override
    public SendPriceChangeRequest mapRow(ResultSet rs, int i) throws SQLException {
        SendPriceChangeRequest sendPriceChangeRequest = new SendPriceChangeRequest();

        sendPriceChangeRequest.setId(rs.getInt("spcrid"));
        sendPriceChangeRequest.setPriceChangeRequestId(rs.getInt("spcrpricechangerequest_id"));
        sendPriceChangeRequest.setSenddata(rs.getString("spcrsenddata"));
        sendPriceChangeRequest.setIsSend(rs.getBoolean("spcrissend"));
        sendPriceChangeRequest.setSendbegindate(rs.getDate("spcrsendbegindate"));
        sendPriceChangeRequest.setSendenddate(rs.getDate("spcrsendenddate"));
        sendPriceChangeRequest.setSendcount(rs.getInt("spcrsendcount"));
        sendPriceChangeRequest.setResponse(rs.getString("spcrresponse"));
        sendPriceChangeRequest.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendPriceChangeRequest.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendPriceChangeRequest.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendPriceChangeRequest.getBranchSetting().getBranch().setLicenceCode(rs.getString("brlicencecode"));
        return sendPriceChangeRequest;
    }

}

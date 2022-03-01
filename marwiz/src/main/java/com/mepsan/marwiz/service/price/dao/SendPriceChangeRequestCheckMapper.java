/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 10:29:58
 */
package com.mepsan.marwiz.service.price.dao;

import com.mepsan.marwiz.general.model.log.SendPriceChangeRequestCheck;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendPriceChangeRequestCheckMapper implements RowMapper<SendPriceChangeRequestCheck> {

    @Override
    public SendPriceChangeRequestCheck mapRow(ResultSet rs, int i) throws SQLException {
        SendPriceChangeRequestCheck sendPriceChangeRequestCheck = new SendPriceChangeRequestCheck();

        sendPriceChangeRequestCheck.setPriceChangeRequestIds(rs.getString("spcrpricechangerequestid"));
        sendPriceChangeRequestCheck.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendPriceChangeRequestCheck.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendPriceChangeRequestCheck.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendPriceChangeRequestCheck.getBranchSetting().getBranch().setLicenceCode(rs.getString("brlicencecode"));
        return sendPriceChangeRequestCheck;
    }

}

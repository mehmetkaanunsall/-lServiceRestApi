/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 10:29:58
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.log.SendStockRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendStockRequestMapper implements RowMapper<SendStockRequest> {

    @Override
    public SendStockRequest mapRow(ResultSet rs, int i) throws SQLException {
        SendStockRequest sendStockRequest = new SendStockRequest();

        try {
            sendStockRequest.setId(rs.getInt("srid"));
            sendStockRequest.setStockrequestId(rs.getInt("srstockrequestid"));
            sendStockRequest.setSenddata(rs.getString("srsenddata"));
            sendStockRequest.setSendbegindate(rs.getDate("srsendbegindate"));
            sendStockRequest.setSendenddate(rs.getDate("srsendenddate"));
            sendStockRequest.setSendcount(rs.getInt("srsendcount"));
            sendStockRequest.setResponse(rs.getString("srresponse"));
            sendStockRequest.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
            sendStockRequest.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
            sendStockRequest.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        } catch (Exception e) {
        }
        try {
            sendStockRequest.getBranchSetting().getBranch().setLicenceCode(rs.getString("srlicencecode"));
        } catch (Exception e) {
        }
        
        sendStockRequest.setIsSend(rs.getBoolean("srissend"));
        return sendStockRequest;
    }

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.08.2019 08:44:19
 */
package com.mepsan.marwiz.service.waste.dao;

import com.mepsan.marwiz.general.model.log.SendWaste;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendWasteMapper implements RowMapper<SendWaste> {

    @Override
    public SendWaste mapRow(ResultSet rs, int i) throws SQLException {
        SendWaste sendWaste = new SendWaste();
        sendWaste.setId(rs.getInt("swid"));
        sendWaste.setWarehouseReceiptId(rs.getInt("swwarehousereceipt_id"));
        sendWaste.setSendData(rs.getString("swsenddate"));
        sendWaste.setIsSend(rs.getBoolean("swissend"));
        sendWaste.setSendBeginDate(rs.getDate("swsendbegindate"));
        sendWaste.setSendEndDate(rs.getDate("swsendenddate"));
        sendWaste.setSendCount(rs.getInt("swsendcount"));
        sendWaste.setResponse(rs.getString("swresponse"));
        sendWaste.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendWaste.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendWaste.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendWaste.getBranchSetting().getBranch().setLicenceCode(rs.getString("brlicencecode"));

        return sendWaste;
    }

}
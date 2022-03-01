/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.09.2018 11:57:06
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.log.SendStockInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendStockInfoMapper implements RowMapper<SendStockInfo> {

    @Override
    public SendStockInfo mapRow(ResultSet rs, int i) throws SQLException {
        SendStockInfo sendStockInfo = new SendStockInfo(); 
        sendStockInfo.setSenddata(rs.getString("senddata"));
        sendStockInfo.getBranchSetting().getBranch().setLicenceCode(rs.getString("licencecode"));
        sendStockInfo.getBranchSetting().getBranch().setId(rs.getInt("branch_id"));
        sendStockInfo.getBranchSetting().setwSendPoint(rs.getString("wsendpoint"));
        sendStockInfo.getBranchSetting().setWebServiceUserName(rs.getString("wsusername"));
        sendStockInfo.getBranchSetting().setWebServicePassword(rs.getString("wspassword"));
        return sendStockInfo;
    }

}

/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 10:29:58
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SendStockRequestCheckMapper implements RowMapper<SendStockRequestCheck> {

    @Override
    public SendStockRequestCheck mapRow(ResultSet rs, int i) throws SQLException {
        SendStockRequestCheck sendStockRequestCheck = new SendStockRequestCheck();

        sendStockRequestCheck.setStockRequestIds(rs.getString("srstockrequestids"));
        sendStockRequestCheck.getBranchSetting().setwSendPoint(rs.getString("brswsendpoint"));
        sendStockRequestCheck.getBranchSetting().setWebServiceUserName(rs.getString("brswsusername"));
        sendStockRequestCheck.getBranchSetting().setWebServicePassword(rs.getString("brswspassword"));
        sendStockRequestCheck.getBranchSetting().getBranch().setLicenceCode(rs.getString("brlicencecode"));
        return sendStockRequestCheck;
    }

}

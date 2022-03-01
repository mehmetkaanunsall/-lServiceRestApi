package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.log.SendEInvoice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class SendEInvoiceMapper implements RowMapper<SendEInvoice> {

    @Override
    public SendEInvoice mapRow(ResultSet rs, int i) throws SQLException {
        SendEInvoice sei = new SendEInvoice();

        sei.setId(rs.getInt("lseiid"));
        sei.setInvoiceId(rs.getInt("lseiinvoice_id"));
        sei.setSendData(rs.getString("lseisenddata"));
        sei.setSendBeginDate(rs.getTimestamp("lseisendbegindate"));
        sei.setSendEndDate(rs.getTimestamp("lseisendenddate"));
        sei.setSendCount(rs.getInt("lseisendcount"));
        sei.setResponseDescription(rs.getString("lseiresponsedescription"));
        sei.setResponseCode(rs.getString("lseiresponsecode"));
        sei.setIsSend(rs.getBoolean("lseiissend"));
        sei.setIntegrationInvoice(rs.getString("lseiintegrationinvoice"));
        sei.setGibInvoice(rs.getString("lseigibinvoice"));
        sei.setInvoiceStatus(rs.getInt("lseinvoicestatus"));
        sei.getBranch().setId(rs.getInt("lseinvoicestatus"));
        try {
            
        } catch (Exception e) {
        }

        return sei;

    }

}

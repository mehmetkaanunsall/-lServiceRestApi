/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 08:29:29
 */
package com.mepsan.marwiz.service.paro.dao;

import com.mepsan.marwiz.service.model.LogParo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ParoOfflineSalesMapper implements RowMapper<LogParo> {

    @Override
    public LogParo mapRow(ResultSet rs, int i) throws SQLException {
        LogParo logParo = new LogParo();
        logParo.setId(rs.getInt("r_prid"));
        logParo.setRequestId(rs.getLong("r_prrequest_id"));
        logParo.setTransactionNo(rs.getString("r_prtransactionno"));
        logParo.setProvisionNo(rs.getString("r_prprovisionno"));
        logParo.setTypeId(rs.getInt("r_prtype_id"));
        logParo.setSaleId(rs.getInt("r_prsale_id"));
        logParo.setIsParoCustomer(rs.getBoolean("r_pris_parocustomer"));
        logParo.setSendData(rs.getString("r_prsenddata"));
        logParo.setIsSuccess(rs.getBoolean("r_pris_success"));
        logParo.setErrorCode(rs.getInt("r_prerrorcode"));
        logParo.setErrorMessage(rs.getString("r_prerrormessage"));
        logParo.setIsSend(rs.getBoolean("r_pris_send"));
        logParo.setSendBeginDate(rs.getTimestamp("r_prsendbegindate"));
        logParo.setSendEndDate(rs.getTimestamp("r_prsendenddate"));
        logParo.setSendCount(rs.getInt("r_prsendcount"));
        logParo.setErrorCount(rs.getInt("r_prerrorcount"));
        logParo.setResponse(rs.getString("r_prresponse"));
        logParo.setBranchId(rs.getInt("r_slbranch_id"));
        logParo.setCreatedId(rs.getInt("r_slc_id"));
        logParo.setIsQRCode(rs.getBoolean("r_pris_qrcode"));
        logParo.setIsInvoice(rs.getBoolean("r_slis_invoice"));
        logParo.setPointOfSaleId(rs.getInt("r_slpointofsale_id"));
        logParo.setOrderId(rs.getString("r_saleorder_id"));
        return logParo;
    }

}

/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:28:10
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InvoicePaymentMapper implements RowMapper<InvoicePayment> {

    @Override
    public InvoicePayment mapRow(ResultSet rs, int i) throws SQLException {
        InvoicePayment ip = new InvoicePayment();

        ip.setId(rs.getInt("inpid"));
        ip.getFinancingDocument().setId(rs.getInt("fdocid"));
        ip.getFinancingDocument().setDocumentNumber(rs.getString("fdocdocumentnumber"));
        ip.getFinancingDocument().setDocumentDate(rs.getTimestamp("fdocdocumentdate"));

        ip.getCredit().setId(rs.getInt("crdid"));
        ip.getCredit().setDueDate(rs.getTimestamp("crdduedate"));

        ip.setPrice(rs.getBigDecimal("inpprice"));
        ip.setExchangeRate(rs.getBigDecimal("inpexchangerate"));
        ip.getCurrency().setId(rs.getInt("inpcurrency_id"));
        ip.getCurrency().setCode(rs.getString("crrcode"));
        ip.getType().setId(rs.getInt("inptype_id"));
        ip.getType().setTag(rs.getString("typdname"));

        ip.setIsDirection(rs.getBoolean("inpis_direction"));

        ip.getChequeBill().setId(rs.getInt("chqid"));
        ip.getChequeBill().setPortfolioNumber(rs.getString("chqportfolionumber"));
        ip.getChequeBill().getDocumentNumber().setId(rs.getInt("chqdocumentnumber_id"));
        if (ip.getChequeBill().getDocumentNumber().getId() > 0) {
            ip.getChequeBill().getDocumentNumber().setActualNumber(rs.getInt("chqdocumentnumber"));
        }
        ip.getChequeBill().setDocumentSerial(rs.getString("chqdocumentserial"));
        ip.getChequeBill().getBankBranch().setId(rs.getInt("chqbankbranch_id"));
        ip.getChequeBill().setAccountNumber(rs.getString("chqaccountnumber"));
        ip.getChequeBill().setIbanNumber(rs.getString("chqibannumber"));
        ip.getChequeBill().setExpiryDate(rs.getTimestamp("chqexpirydate"));
        ip.getChequeBill().getStatus().setId(rs.getInt("chqstatus_id"));
        ip.getChequeBill().getPaymentCity().setId(rs.getInt("chqpaymentcity_id"));
        ip.getChequeBill().setBillCollocationDate(rs.getTimestamp("chqbill_collocationdate"));
        ip.getChequeBill().getCountry().setId(rs.getInt("ctycountry_id"));
        ip.getChequeBill().setAccountGuarantor(rs.getString("chqaccountguarantor"));

        ip.getSafe().setId(rs.getInt("smsafe_id"));
        ip.getBankAccount().setId(rs.getInt("bambankaccount_id"));

        if (ip.getChequeBill().getId() > 0) {
            ip.setProcessDate(rs.getTimestamp("chqexpirydate"));
        } else if (ip.getFinancingDocument().getId() > 0) {
            ip.setProcessDate(rs.getTimestamp("fdocdocumentdate"));
        } else if (ip.getCredit().getId() > 0) {
            ip.setProcessDate(rs.getTimestamp("crdduedate"));
        }

        return ip;
    }

}

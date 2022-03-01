/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import com.mepsan.marwiz.general.model.finance.CreditPayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Gozde Gursel
 */
public class CreditPaymentMapper implements RowMapper<CreditPayment> {

    @Override
    public CreditPayment mapRow(ResultSet rs, int i) throws SQLException {
        CreditPayment creditPayment = new CreditPayment();
        creditPayment.setId(rs.getInt("cpyid"));
        creditPayment.getCredit().setId(rs.getInt("cpycredit_id"));
        creditPayment.setProcessDate(rs.getTimestamp("cpyprocessdate"));
        creditPayment.setPrice(rs.getBigDecimal("cpyprice"));
        creditPayment.getType().setId(rs.getInt("cpytype_id"));
        creditPayment.getType().setTag(rs.getString("typdname"));
        creditPayment.getCurrency().setId(rs.getInt("cpycurrency_id"));
        creditPayment.getCurrency().setCode(rs.getString("crycode"));
        creditPayment.getCurrency().setTag(rs.getString("crycode"));
        creditPayment.setExchangeRate(rs.getBigDecimal("cpyexchangerate"));
        creditPayment.getFinancingDocument().setDocumentNumber(rs.getString("fddocumentnumber"));
        creditPayment.setIsDirection(rs.getBoolean("cpyis_direction"));

        if (creditPayment.getType().getId() == 66 || creditPayment.getType().getId() == 69) {
            creditPayment.getFinancingDocument().setDocumentNumber(rs.getString("chqdocumentnumber"));
        }
        creditPayment.getFinancingDocument().setDescription(rs.getString("fddescription"));
        creditPayment.getBankAccount().setId(rs.getInt("bambankaccount_id"));
        creditPayment.getBankAccount().setName(rs.getString("bankname"));
        creditPayment.getSafe().setId(rs.getInt("sfmsafe_id"));
        creditPayment.getSafe().setName(rs.getString("sfname"));

        creditPayment.getChequeBill().setId(rs.getInt("chqid"));
        creditPayment.getChequeBill().setPortfolioNumber(rs.getString("chqportfolionumber"));
        creditPayment.getChequeBill().getDocumentNumber().setId(rs.getInt("chqdocumentnumber_id"));
        if (creditPayment.getChequeBill().getDocumentNumber().getId() > 0) {
            creditPayment.getChequeBill().getDocumentNumber().setActualNumber(rs.getInt("chqdocumentnumber"));
        }
//        creditPayment.getChequeBill().getAccount().setId(rs.getInt("accid"));
//        creditPayment.getChequeBill().getAccount().setName(rs.getString("accname"));
        creditPayment.getChequeBill().setDocumentSerial(rs.getString("chqdocumentserial"));
        creditPayment.getChequeBill().getBankBranch().setId(rs.getInt("chqbankbranch_id"));
        creditPayment.getChequeBill().setAccountNumber(rs.getString("chqaccountnumber"));
        creditPayment.getChequeBill().setIbanNumber(rs.getString("chqibannumber"));
        creditPayment.getChequeBill().setExpiryDate(rs.getTimestamp("chqexpirydate"));
        creditPayment.getChequeBill().getStatus().setId(rs.getInt("chqstatus_id"));
        creditPayment.getChequeBill().getPaymentCity().setId(rs.getInt("chqpaymentcity_id"));
        creditPayment.getChequeBill().setBillCollocationDate(rs.getTimestamp("chqbill_collocationdate"));
        creditPayment.getChequeBill().getCountry().setId(rs.getInt("ctycountry_id"));
        creditPayment.getChequeBill().setAccountGuarantor(rs.getString("chqaccountguarantor"));

        return creditPayment;
    }

}

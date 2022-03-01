/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.dao;

import com.mepsan.marwiz.general.model.finance.ChequeBill;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBillMapper implements RowMapper<ChequeBill> {

    @Override
    public ChequeBill mapRow(ResultSet rs, int i) throws SQLException {
        ChequeBill chequeBill = new ChequeBill();
        chequeBill.setId(rs.getInt("cqbid"));
        chequeBill.getAccount().setId(rs.getInt("cqbaccount_id"));
        chequeBill.getAccount().setName(rs.getString("accname"));
        chequeBill.getAccount().setTitle(rs.getString("acctitle"));
        chequeBill.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        chequeBill.setAccountNumber(rs.getString("cqbaccountnumber"));
        chequeBill.getBankBranch().setId(rs.getInt("cqbbankbranch_id"));
        chequeBill.getBankBranch().setName(rs.getString("bkbname"));
        chequeBill.setBillCollocationDate(rs.getDate("cqbbill_collocationdate"));
        chequeBill.getCurrency().setId(rs.getInt("cqbcurrency_id"));
        chequeBill.getDocumentNumber().setId(rs.getInt("cqbdocumentnumber_id"));
        chequeBill.setDocumentNo(rs.getString("cqbdocumnetnumber"));
        chequeBill.setDocumentSerial(rs.getString("cqbdocumentserial"));
        chequeBill.setExchangeRate(rs.getBigDecimal("cqbexchangerate"));
        chequeBill.setExpiryDate(rs.getDate("cqbexpirydate"));
        chequeBill.setIbanNumber(rs.getString("cqbibannumber"));
        chequeBill.setIsCheque(rs.getBoolean("cqbis_cheque"));
        chequeBill.setIsCustomer(rs.getBoolean("cqbis_customer"));
        chequeBill.getCountry().setId(rs.getInt("ctryid"));
        chequeBill.getPaymentCity().setId(rs.getInt("cqbpaymentcity_id"));
        chequeBill.getPaymentCity().setTag(rs.getString("ctydname"));
        chequeBill.setPortfolioNumber(rs.getString("cqbportfolionumber"));
        chequeBill.getEndorsedAccount().setId(rs.getInt("cqbendorsedaccount_id"));
        chequeBill.getEndorsedAccount().setName(rs.getString("acc2name"));
        chequeBill.getEndorsedAccount().setTitle(rs.getString("acc2title"));
        chequeBill.getEndorsedAccount().setIsEmployee(rs.getBoolean("acc2is_employee"));
        chequeBill.getStatus().setId(rs.getInt("cqbstatus_id"));
        chequeBill.getStatus().setTag(rs.getString("sttdname"));
        chequeBill.setTotalMoney(rs.getBigDecimal("cqbtotalmoney"));
        chequeBill.setRemainingMoney(rs.getBigDecimal("cqbremainingmoney"));
        chequeBill.getBranch().setId(rs.getInt("cqbbranch_id"));
        chequeBill.getBranch().setName(rs.getString("brname"));
        chequeBill.getCollectingBankAccount().setId(rs.getInt("cqbcollectingbankaccount_id"));
        chequeBill.getCollectingBankAccount().setName(rs.getString("baname"));

        return chequeBill;
    }

}

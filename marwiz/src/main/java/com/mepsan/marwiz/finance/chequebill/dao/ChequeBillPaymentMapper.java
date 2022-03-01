/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.dao;

import com.mepsan.marwiz.general.model.finance.ChequeBillPayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBillPaymentMapper implements RowMapper<ChequeBillPayment>{

    @Override
    public ChequeBillPayment mapRow(ResultSet rs, int i) throws SQLException {
        ChequeBillPayment chequeBillPayment=new ChequeBillPayment();
        chequeBillPayment.setId(rs.getInt("cqbpid"));
        chequeBillPayment.setProcessDate(rs.getTimestamp("cqbpprocessdate"));
        chequeBillPayment.setPrice(rs.getBigDecimal("cqbpprice"));
        chequeBillPayment.getType().setId(rs.getInt("cqbptype_id"));
        chequeBillPayment.getType().setTag(rs.getString("typdname"));
        chequeBillPayment.getCurrency().setId(rs.getInt("cqbpcurrency_id"));
        chequeBillPayment.getCurrency().setCode(rs.getString("crycode"));
        chequeBillPayment.getCurrency().setTag(rs.getString("crycode"));
        chequeBillPayment.setExchangeRate(rs.getBigDecimal("cqbpexchangerate"));
        chequeBillPayment.getFinancingDocument().setDocumentNumber(rs.getString("fddocumentnumber"));
        chequeBillPayment.getFinancingDocument().setDescription(rs.getString("fddescription"));
        chequeBillPayment.getBankAccount().setId(rs.getInt("bambankaccount_id"));
        chequeBillPayment.getBankAccount().setName(rs.getString("bankname"));
        chequeBillPayment.getSafe().setId(rs.getInt("sfmsafe_id"));
        chequeBillPayment.getSafe().setName(rs.getString("sfname"));
        
        return chequeBillPayment;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.dao;

import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseMovementMapper implements RowMapper<IncomeExpenseMovement> {

    @Override
    public IncomeExpenseMovement mapRow(ResultSet rs, int i) throws SQLException {

        IncomeExpenseMovement incomeExpenseMovement = new IncomeExpenseMovement();
        incomeExpenseMovement.setId(rs.getInt("fiemid"));       
        incomeExpenseMovement.setPrice(rs.getBigDecimal("fiemprice"));
        incomeExpenseMovement.getCurrency().setId(rs.getInt("fiemcurrency_id"));

        try {
            incomeExpenseMovement.getFinancingDocument().setId(rs.getInt("fdocid"));
            incomeExpenseMovement.setMovementDate(rs.getTimestamp("fiemmovementdate"));
            
            incomeExpenseMovement.getFinancingDocument().setDocumentNumber(rs.getString("fdocdocumentnumber"));
            incomeExpenseMovement.getFinancingDocument().setDescription(rs.getString("fdocdescription"));
            incomeExpenseMovement.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
            incomeExpenseMovement.getFinancingDocument().getFinancingType().setTag(rs.getString("typdname"));

            incomeExpenseMovement.setExchangeRate(rs.getBigDecimal("fiemexchangerate"));

            incomeExpenseMovement.getCurrency().setTag(rs.getString("crrcode"));
            incomeExpenseMovement.getCurrency().setSign(rs.getString("crrsign"));
            
        } catch (Exception e) {

        }
        try {
            incomeExpenseMovement.getFinancingDocument().setBankAccountCommissionId(rs.getInt("bacid"));
        } catch (Exception e) {
        }
        return incomeExpenseMovement;
    }

}

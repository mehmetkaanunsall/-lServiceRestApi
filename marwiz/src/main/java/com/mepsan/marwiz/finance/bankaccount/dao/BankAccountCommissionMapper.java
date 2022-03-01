/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.08.2020 03:47:46
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankAccountCommissionMapper implements RowMapper<BankAccountCommission> {

    @Override
    public BankAccountCommission mapRow(ResultSet rs, int i) throws SQLException {
        BankAccountCommission bankAccountCommission = new BankAccountCommission();
        bankAccountCommission.setId(rs.getInt("bacid"));
        bankAccountCommission.getFinancingDocument().setId(rs.getInt("bacfinancingdocument_id"));
        bankAccountCommission.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
        bankAccountCommission.getFinancingDocument().setDocumentNumber(rs.getString("fdocdocumentnumber"));
        bankAccountCommission.getFinancingDocument().setPrice(rs.getBigDecimal("fdocprice"));
        bankAccountCommission.getFinancingDocument().getCurrency().setId(rs.getInt("fdoccurrency_id"));
        bankAccountCommission.getFinancingDocument().setExchangeRate(rs.getBigDecimal("fdocexchangerate"));
        bankAccountCommission.getFinancingDocument().setDocumentDate(rs.getTimestamp("fdocdocumentdate"));
        bankAccountCommission.getFinancingDocument().setDescription(rs.getString("fdocdescription"));
        bankAccountCommission.getFinancingDocument().setInMovementId(rs.getInt("bambankaccount_id"));
        bankAccountCommission.getFinancingDocument().setOutMovementId(rs.getInt("bam2bankaccount_id"));
        bankAccountCommission.getFinancingDocument().getBranch().setId(rs.getInt("fdocbranch_id"));
        bankAccountCommission.getFinancingDocument().getTransferBranch().setId(rs.getInt("fdoctransferbranch_id"));

        bankAccountCommission.getCommissionFinancingDocument().setId(rs.getInt("baccommissionfinancingdocument_id"));
        bankAccountCommission.getCommissionFinancingDocument().getFinancingType().setId(rs.getInt("fdoc2type_id"));
        bankAccountCommission.getCommissionFinancingDocument().getIncomeExpense().setId(rs.getInt("inxmincomeexpense_id"));
        bankAccountCommission.getCommissionFinancingDocument().setDocumentNumber(rs.getString("fdoc2documentnumber"));
        bankAccountCommission.getCommissionFinancingDocument().setPrice(rs.getBigDecimal("fdoc2price"));
        bankAccountCommission.getCommissionFinancingDocument().getCurrency().setId(rs.getInt("fdoc2currency_id"));
        bankAccountCommission.getCommissionFinancingDocument().setExchangeRate(rs.getBigDecimal("fdoc2exchangerate"));
        bankAccountCommission.getCommissionFinancingDocument().setDocumentDate(rs.getTimestamp("fdoc2documentdate"));
        bankAccountCommission.getCommissionFinancingDocument().setDescription(rs.getString("fdoc2description"));
        bankAccountCommission.getCommissionFinancingDocument().setInMovementId(rs.getInt("inxmincomeexpense_id"));
        bankAccountCommission.getCommissionFinancingDocument().setOutMovementId(rs.getInt("bam3bankaccount_id"));
        bankAccountCommission.getCommissionFinancingDocument().getBranch().setId(rs.getInt("fdoc2branch_id"));
        bankAccountCommission.getCommissionFinancingDocument().getTransferBranch().setId(rs.getInt("fdoc2transferbranch_id"));

        bankAccountCommission.setTotalMoney(rs.getBigDecimal("bactotalmoney"));
        bankAccountCommission.setCommissionRate(rs.getBigDecimal("baccommissionrate"));
        bankAccountCommission.setCommissionMoney(rs.getBigDecimal("baccommissionmoney"));

        return bankAccountCommission;
    }
}

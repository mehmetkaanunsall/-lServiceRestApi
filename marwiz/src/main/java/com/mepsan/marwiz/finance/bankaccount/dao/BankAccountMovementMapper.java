/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankAccountMovementMapper implements RowMapper<BankAccountMovement> {

    @Override
    public BankAccountMovement mapRow(ResultSet rs, int i) throws SQLException {
        BankAccountMovement accountMovement = new BankAccountMovement();
        accountMovement.setId(rs.getInt("bkamid"));
        try{
        accountMovement.getFinancingDocument().setId(rs.getInt("fdocid"));
        accountMovement.getFinancingDocument().setDocumentNumber(rs.getString("fdocdocumentnumber"));
        accountMovement.getFinancingDocument().setDescription(rs.getString("fdocdescription"));
        accountMovement.getFinancingDocument().getIncomeExpense().setId(rs.getInt("fiemincomeexpense_id"));
        accountMovement.getFinancingDocument().getIncomeExpense().setName(rs.getString("fiename"));
        accountMovement.getFinancingDocument().setDocumentDate(rs.getTimestamp("fdocdocumnetdate"));
        accountMovement.setBalance(rs.getBigDecimal("bkabalance"));
        accountMovement.setPrice(rs.getBigDecimal("bkamprice"));
        accountMovement.setIsDirection(rs.getBoolean("bkamis_direction"));

        accountMovement.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
        accountMovement.getFinancingDocument().getFinancingType().setTag(rs.getString("typdname"));

        UserData createUserData = new UserData(rs.getInt("fdocc_id"));
        accountMovement.setUserCreated(createUserData);
        accountMovement.getUserCreated().setName(rs.getString("usrname"));
        accountMovement.getUserCreated().setSurname(rs.getString("usrsurname"));
        accountMovement.setDateCreated(rs.getTimestamp("fdocc_time"));

        UserData updateUserData = new UserData(rs.getInt("fdocu_id"));
        accountMovement.setUserUpdated(updateUserData);
        accountMovement.getUserUpdated().setId(rs.getInt("fdocu_id"));
        accountMovement.getUserUpdated().setName(rs.getString("usr1name"));
        accountMovement.getUserUpdated().setSurname(rs.getString("usr1surname"));
        accountMovement.setDateUpdated(rs.getTimestamp("fdocu_time"));
        accountMovement.getBankAccount().getCurrency().setId(rs.getInt("bkacurrency_id"));
        } catch (Exception e) {
        }

        try {
            accountMovement.setTransferringbalance(rs.getBigDecimal("transferringbalance"));
        } catch (Exception e) {
        }
        try {
            accountMovement.setTotalIncoming(rs.getBigDecimal("sumincoming"));

        } catch (Exception e) {
        }
        try {
            accountMovement.setTotalOutcoming(rs.getBigDecimal("sumoutcoming"));

        } catch (Exception e) {
        }
        try {
            accountMovement.getBranch().setId(rs.getInt("bkambranch_id"));
            accountMovement.getBranch().setName(rs.getString("brname"));
        } catch (Exception e) {
        }
        try {
            accountMovement.getFinancingDocument().setBankAccountCommissionId(rs.getInt("bacid"));
        } catch (Exception e) {
        }
        return accountMovement;
    }

}

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 10:01:20
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BankAccountMapper implements RowMapper<BankAccount> {

    @Override
    public BankAccount mapRow(ResultSet rs, int i) throws SQLException {
        BankAccount bankAccount = new BankAccount();

        bankAccount.setId(rs.getInt("bkaid"));
        bankAccount.setName(rs.getString("bkaname"));
        try {
            bankAccount.getCurrency().setId(rs.getInt("bkacurrency_id"));
            bankAccount.setAccountNumber(rs.getString("bkaaccountnumber"));
            bankAccount.getBankBranch().getBank().setId(rs.getInt("bkbbank_id"));
            bankAccount.getBankBranch().getBank().setName(rs.getString("bnkname"));
            bankAccount.getBankBranch().setId(rs.getInt("bkabankbranch_id"));
            bankAccount.getBankBranch().setName(rs.getString("bkbname"));
        } catch (Exception e) {
        }

        try {
            bankAccount.getCurrency().setTag(rs.getString("crrdname"));
        } catch (Exception e) {
        }
        try {
            bankAccount.getCurrency().setCode(rs.getString("crrcode"));
        } catch (Exception e) {
        }
        try {
            bankAccount.getCurrency().setSign(rs.getString("crrsign"));
            bankAccount.getBankAccountBranchCon().setBalance(rs.getBigDecimal("bbcbalance"));
            bankAccount.getBankBranch().setCode(rs.getString("bkbcode"));
            bankAccount.setIbanNumber(rs.getString("bkaibannumber"));
            bankAccount.getCurrency().setTag(rs.getString("crrdname"));
            bankAccount.getType().setId(rs.getInt("bkatype_id"));
            bankAccount.getType().setTag(rs.getString("typdname"));
            bankAccount.getStatus().setId(rs.getInt("bkastatus_id"));
            bankAccount.getStatus().setTag(rs.getString("sttdname"));
            bankAccount.getBankBranch().setCode(rs.getString("bkbcode"));

        } catch (Exception e) {
        }
        try {
            bankAccount.getBankAccountBranchCon().setCommissionRate(rs.getBigDecimal("bbccommissionrate"));
            BankAccount commissionBankAccount = new BankAccount();
            commissionBankAccount.setId(rs.getInt("bbccommissionbankaccount_id"));
            commissionBankAccount.setName(rs.getString("bka1name"));
            bankAccount.getBankAccountBranchCon().setCommissionBankAccount(commissionBankAccount);
            bankAccount.getBankAccountBranchCon().getCommissionIncomeExpense().setId(rs.getInt("bbccommissionincomeexpense_id"));
            bankAccount.getBankAccountBranchCon().getCommissionIncomeExpense().setName(rs.getString("iename"));
        } catch (Exception e) {
        }
        try {
            bankAccount.setCreditCardLimit(rs.getBigDecimal("bkacreditcardlimit"));
            bankAccount.setCutOffDate(rs.getDate("bkacutoffdate"));
            bankAccount.setPaymentDueDate(rs.getDate("bkapaymentduedate"));
        } catch (Exception e) {
        }

        return bankAccount;

    }

}

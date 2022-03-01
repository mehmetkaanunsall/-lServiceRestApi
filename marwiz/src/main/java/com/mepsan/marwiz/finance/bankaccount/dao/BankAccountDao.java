/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:48:13
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankAccountDao extends JdbcDaoSupport implements IBankAccountDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<BankAccount> findAll() {

        String sql = "SELECT \n"
                + "    bka.id AS bkaid,\n"
                + "    bka.name AS bkaname,\n"
                + "    bka.accountnumber AS bkaaccountnumber,\n"
                + "    bka.ibannumber AS bkaibannumber,\n"
                + "    bka.currency_id AS bkacurrency_id,\n"
                + "    crrd.name AS crrdname,\n"
                + "    crr.sign AS crrsign,\n"
                + "    crr.code as crrcode,\n"
                + "    bka.type_id AS bkatype_id,\n"
                + "    typd.name AS typdname,\n"
                + "    bka.status_id AS bkastatus_id,\n"
                + "    sttd.name AS sttdname,\n"
                + "    bbc.balance AS bbcbalance,\n"
                + "    bkb.code AS bkbcode,\n"
                + "    bkb.bank_id AS bkbbank_id,\n"
                + "    bnk.name AS bnkname,\n"
                + "    bka.bankbranch_id AS bkabankbranch_id,\n"
                + "    bkb.name AS bkbname,\n"
                + "    bbc.commissionbankaccount_id AS bbccommissionbankaccount_id,\n"
                + "    bka1.name AS bka1name,\n"
                + "    bbc.commissionrate AS bbccommissionrate,\n"
                + "    bbc.commissionincomeexpense_id AS bbccommissionincomeexpense_id,\n"
                + "    ie.name AS iename,\n"
                + "    bka.creditcardlimit AS bkacreditcardlimit,\n"
                + "    bka.cutoffdate AS bkacutoffdate,\n"
                + "    bka.paymentduedate AS bkapaymentduedate\n"
                + "FROM\n"
                + "    finance.bankaccount bka \n"
                + "    INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = bka.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                + "    INNER JOIN finance.bankbranch bkb ON (bkb.id = bka.bankbranch_id AND bkb.deleted = False)\n"
                + "    INNER JOIN finance.bank bnk ON (bnk.id = bkb.bank_id AND bnk.deleted = False)\n"
                + "    INNER JOIN system.currency crr ON (crr.id = bka.currency_id)\n"
                + "    INNER JOIN system.currency_dict crrd ON (crrd.currency_id = bka.currency_id AND crrd.language_id = ?)\n"
                + "    INNER JOIN system.type_dict typd ON (typd.type_id = bka.type_id AND typd.language_id = ?)\n"
                + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = bka.status_id AND sttd.language_id = ?) \n"
                + "    LEFT JOIN finance.bankaccount bka1 ON(bka1.id = bbc.commissionbankaccount_id AND bka1.deleted = fALSE)\n"
                + "    LEFT JOIN finance.incomeexpense ie ON (ie.id = bbc.commissionincomeexpense_id AND ie.deleted=FALSE)\n"
                + "WHERE\n"
                + " bka.deleted = FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, param, new BankAccountMapper());
    }

    @Override
    public int create(BankAccount obj) {

        String sql = "SELECT r_bankaccount_id FROM finance.process_bankaccount (? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?,?,?,?);";

        Object[] param = new Object[]{0, null, obj.getBankBranch().getId(), obj.getName(),
            obj.getAccountNumber(), obj.getIbanNumber(), obj.getCurrency().getId(), obj.getType().getId(), obj.getStatus().getId(),
            obj.getBankAccountBranchCon().getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? obj.getBankAccountBranchCon().getBalance() : obj.getBankAccountBranchCon().getBalance().multiply(BigDecimal.valueOf(-1)),
            obj.getBankAccountBranchCon().getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false, new Date(), obj.getBankAccountBranchCon().getCommissionRate(),
            obj.getBankAccountBranchCon().getCommissionBankAccount().getId() == 0 ? null : obj.getBankAccountBranchCon().getCommissionBankAccount().getId(),
            obj.getBankAccountBranchCon().getCommissionIncomeExpense().getId() == 0 ? null : obj.getBankAccountBranchCon().getCommissionIncomeExpense().getId(),
            sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getCreditCardLimit(), obj.getCutOffDate(), obj.getPaymentDueDate()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(BankAccount obj) {

        String sql = "SELECT r_bankaccount_id FROM finance.process_bankaccount (? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?, ? ,?, ?);";

        Object[] param = new Object[]{1, obj.getId(), obj.getBankBranch().getId(), obj.getName(),
            obj.getAccountNumber(), obj.getIbanNumber(), obj.getCurrency().getId(), obj.getType().getId(), obj.getStatus().getId(),
            0, false,
            new Date(), obj.getBankAccountBranchCon().getCommissionRate(),
            obj.getBankAccountBranchCon().getCommissionBankAccount().getId() == 0 ? null : obj.getBankAccountBranchCon().getCommissionBankAccount().getId(),
            obj.getBankAccountBranchCon().getCommissionIncomeExpense().getId() == 0 ? null : obj.getBankAccountBranchCon().getCommissionIncomeExpense().getId(),
            sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getCreditCardLimit(), obj.getCutOffDate(), obj.getPaymentDueDate()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(BankAccount bankAccount) {
        String sql = "";
        Object[] param = null;
        
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {

            sql = "UPDATE finance.bankaccount set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                    + "UPDATE finance.bankaccountmovement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND bankaccount_id=?;\n"
                    + "UPDATE finance.bankaccount_branch_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND bankaccount_id=?;\n";

            param = new Object[]{sessionBean.getUser().getId(), bankAccount.getId(), sessionBean.getUser().getId(), bankAccount.getId(),
                sessionBean.getUser().getId(), bankAccount.getId()};

        } else {

            sql = "UPDATE finance.bankaccountmovement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND bankaccount_id=? AND branch_id=?;\n"
                    + "UPDATE finance.bankaccount_branch_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND bankaccount_id=? AND branch_id=?;\n";

            param = new Object[]{sessionBean.getUser().getId(), bankAccount.getId(), sessionBean.getUser().getLastBranch().getId(),
                sessionBean.getUser().getId(), bankAccount.getId(), sessionBean.getUser().getLastBranch().getId()};
        }

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<BankAccount> bankAccountForSelect(String where, Branch branch) {
        String sql = "SELECT  \n"
                + "     bka.id AS bkaid, \n"
                + "     bka.name AS bkaname,  \n"
                + "     bka.currency_id AS bkacurrency_id,  \n"
                + "     crrd.name as crrdname, \n"
                + "     crr.code as crrcode, \n"
                + "     bka.accountnumber AS bkaaccountnumber, \n"
                + "     bnk.id AS bkbbank_id, \n"
                + "     bnk.name AS bnkname, \n"
                + "     bkb.id AS bkabankbranch_id, \n"
                + "     bkb.name AS bkbname \n"
                + " FROM \n"
                + "     finance.bankaccount bka\n"
                + "     INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = bka.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                + "     INNER JOIN finance.bankbranch bkb ON (bkb.id = bka.bankbranch_id AND bkb.deleted = False) \n"
                + "     INNER JOIN finance.bank bnk ON (bnk.id = bkb.bank_id AND bnk.deleted = False) \n"
                + "     INNER JOIN system.currency crr ON (crr.id = bka.currency_id) \n"
                + "     INNER JOIN system.currency_dict crrd ON (crrd.currency_id = bka.currency_id AND crrd.language_id=?) \n"
                + " WHERE \n"
                + "     bka.deleted =false AND bka.status_id=21 \n"
                + where;

        Object[] param = new Object[]{branch.getId(), sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, param, new BankAccountMapper());
    }

    @Override
    public List<BankAccount> bankAccountForSelect(String where, List<Branch> branchList) {

        String branchId = "";
        for (Branch bs : branchList) {
            branchId = branchId + "," + String.valueOf(bs.getId());
            if (bs.getId() == 0) {
                branchId = "";
                break;
            }
        }
        if (!branchId.equals("")) {
            branchId = branchId.substring(1, branchId.length());
            where = where + " AND bka.id IN (select bbc.bankaccount_id from finance.bankaccount_branch_con bbc where bbc.deleted=false and bbc.branch_id IN ( " + branchId + " ) ) ";
        }

        String sql = "SELECT  \n"
                + "     bka.id AS bkaid, \n"
                + "     bka.name AS bkaname,  \n"
                + "     bka.currency_id AS bkacurrency_id,  \n"
                + "     crrd.name as crrdname, \n"
                + "     crr.code as crrcode, \n"
                + "     bka.accountnumber AS bkaaccountnumber, \n"
                + "     bnk.id AS bkbbank_id, \n"
                + "     bnk.name AS bnkname, \n"
                + "     bkb.id AS bkabankbranch_id, \n"
                + "     bkb.name AS bkbname \n"
                + " FROM \n"
                + "     finance.bankaccount bka\n"
                + "     INNER JOIN finance.bankbranch bkb ON (bkb.id = bka.bankbranch_id AND bkb.deleted = False) \n"
                + "     INNER JOIN finance.bank bnk ON (bnk.id = bkb.bank_id AND bnk.deleted = False) \n"
                + "     INNER JOIN system.currency crr ON (crr.id = bka.currency_id) \n"
                + "     INNER JOIN system.currency_dict crrd ON (crrd.currency_id = bka.currency_id AND crrd.language_id=?) \n"
                + " WHERE \n"
                + "     bka.deleted =false AND bka.status_id=21  \n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, param, new BankAccountMapper());
    }

}

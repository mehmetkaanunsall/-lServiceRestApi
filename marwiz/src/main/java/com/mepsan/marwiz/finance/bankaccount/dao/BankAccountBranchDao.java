/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:17:36
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankAccountBranchDao extends JdbcDaoSupport implements IBankAccountBranchDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<BankAccountBranchCon> findBankAccountBranchCon(BankAccount bankAccount) {
        String sql = "SELECT \n"
                + "	 babc.id AS babcid,\n"
                + "    babc.branch_id babcbranch_id,\n"
                + "    br.name AS brname,\n"
                + "    br.is_central AS bris_central,"
                + "    babc.balance AS babcbalance\n"
                + "FROM finance.bankaccount_branch_con babc\n"
                + "INNER JOIN general.branch br ON(br.id = babc.branch_id AND br.deleted = FALSE)\n"
                + "WHERE babc.deleted=FALSE AND babc.bankaccount_id = ?\n"
                + "AND br.id IN (SELECT \n"
                + "   aut.branch_id\n"
                + "FROM general.userdata_authorize_con usda\n"
                + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                + "WHERE usda.deleted=FALSE AND usda.userdata_id=?)";

        Object[] param = new Object[]{bankAccount.getId(), sessionBean.getUser().getId()};

        List<BankAccountBranchCon> result = getJdbcTemplate().query(sql, param, new BankAccountBranchMapper());
        return result;
    }

    @Override
    public int update(BankAccountBranchCon obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int create(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount) {
        String sql = "INSERT INTO finance.bankaccount_branch_con (bankaccount_id, balance, branch_id, c_id, u_id) VALUES (?, ?, ?, ?, ?) RETURNING id;";

        Object[] param = new Object[]{bankAccount.getId(), 0, bankAccountBranchCon.getBranch().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createBeginningMovement(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount) {
        String sql = "INSERT INTO \n"
                + "   finance.bankaccountmovement\n"
                + "   (\n"
                + "        bankaccount_id,\n"
                + "        financingdocument_id,\n"
                + "        is_direction,\n"
                + "        price,\n"
                + "        movementdate,\n"
                + "        branch_id,\n"
                + "        c_id,\n"
                + "        u_id\n"
                + "   )\n"
                + "   VALUES (\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?,\n"
                + "        ?\n"
                + "  ) RETURNING id;";

        Object[] param = new Object[]{bankAccount.getId(), null,
            bankAccountBranchCon.getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false,
            bankAccountBranchCon.getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? bankAccountBranchCon.getBalance() : bankAccountBranchCon.getBalance().multiply(BigDecimal.valueOf(-1)),
            new Date(), bankAccountBranchCon.getBranch().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount) {

        String sql = "UPDATE finance.bankaccount_branch_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=? AND branch_id=?;\n"
                + "UPDATE finance.bankaccountmovement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND bankaccount_id=? AND branch_id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), bankAccountBranchCon.getId(), bankAccountBranchCon.getBranch().getId(),
            sessionBean.getUser().getId(), bankAccount.getId(), bankAccountBranchCon.getBranch().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDeleteBankAccount(BankAccount bankAccount) {
        String sql = "";
        Object[] param = null;
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {

            sql = "       SELECT CASE WHEN (SELECT\n"
                    + "                       				  COUNT(bankaccount_id) \n"
                    + "                       			    FROM \n"
                    + "                                   	finance.bankaccountmovement bkam\n"
                    + "                                          WHERE \n"
                    + "                                           bankaccount_id=? AND deleted=False \n"
                    + "                                           AND  bkam.financingdocument_id IS NOT NULL )>0 THEN 1 ELSE 0 END";

            param = new Object[]{bankAccount.getId()};

        } else {
            sql = "       SELECT CASE WHEN (SELECT\n"
                    + "                       				  COUNT(bankaccount_id) \n"
                    + "                       			    FROM \n"
                    + "                                   	finance.bankaccountmovement bkam\n"
                    + "                                          WHERE \n"
                    + "                                           bankaccount_id=? AND deleted=False  AND branch_id =? \n"
                    + "                                           AND  bkam.financingdocument_id IS NOT NULL)>0 THEN 1 ELSE 0 END   ";

            param = new Object[]{bankAccount.getId(), sessionBean.getUser().getLastBranch().getId()};
        }
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}

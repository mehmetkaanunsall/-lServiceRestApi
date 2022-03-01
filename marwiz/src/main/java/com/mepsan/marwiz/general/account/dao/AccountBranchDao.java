/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class AccountBranchDao extends JdbcDaoSupport implements IAccountBranchDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AccountBranchCon> findAccountBranchCon(Account account) {
        String sql = "SELECT \n"
                + "	 abc.id AS abcid,\n"
                + "    abc.branch_id abcbranch_id,\n"
                + "    br.name AS brname,\n"
                + "    abc.balance AS abcbalance\n"
                + "FROM general.account_branch_con abc\n"
                + "INNER JOIN general.branch br ON(br.id = abc.branch_id AND br.deleted = FALSE)\n"
                + "WHERE abc.deleted=FALSE AND abc.account_id = ? \n"
                + "AND br.id IN (SELECT \n"
                + "   aut.branch_id\n"
                + "FROM general.userdata_authorize_con usda\n"
                + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                + "WHERE usda.deleted=FALSE AND usda.userdata_id=?)";

        Object[] param = new Object[]{account.getId(),sessionBean.getUser().getId()};

        List<AccountBranchCon> result = getJdbcTemplate().query(sql, param, new AccountBranchMapper());
        return result;
    }

    @Override
    public int create(AccountBranchCon obj) {
        
        String sql = "SELECT r_account_id FROM general.insert_account_branch_con (? , ? , ? , ? , ? , ? , ?);";

        Object[] param = new Object[]{obj.getAccount().getId(), obj.getBranch().getId(), obj.getAccount().isIsEmployee(),
            obj.getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false,
            obj.getBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? obj.getBalance() : obj.getBalance().multiply(BigDecimal.valueOf(-1)),
            sessionBean.getUser().getLastBranch().getCurrency().getId(),sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
        
      
    }

    @Override
    public int update(AccountBranchCon obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int testBeforeDelete(AccountBranchCon accountBranchCon) {
        String sql = "SELECT COUNT(abc.id) FROM general.accountmovement abc WHERE abc.deleted =FALSE AND abc.account_id =? AND abc.branch_id = ?";
        Object[] param = new Object[]{accountBranchCon.getAccount().getId(), accountBranchCon.getBranch().getId()};
        
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(AccountBranchCon accountBranchCon) {
        String sql = "UPDATE general.account_branch_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                + "UPDATE general.accountmovement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND account_id=? AND branch_id=?;\n"
                + "UPDATE general.accountinfo set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND account_id=? AND branch_id=?;\n"
                + "UPDATE general.employeeinfo set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND account_id=? AND branch_id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), accountBranchCon.getId(),
            sessionBean.getUser().getId(), accountBranchCon.getAccount().getId(), accountBranchCon.getBranch().getId(),
            sessionBean.getUser().getId(), accountBranchCon.getAccount().getId(), accountBranchCon.getBranch().getId(),
            sessionBean.getUser().getId(), accountBranchCon.getAccount().getId(), accountBranchCon.getBranch().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}

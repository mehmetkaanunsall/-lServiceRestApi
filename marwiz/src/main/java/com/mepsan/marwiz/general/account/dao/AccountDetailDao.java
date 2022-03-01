/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.AccountInfo;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author samet.dag
 */
public class AccountDetailDao extends JdbcDaoSupport implements IAccountDetailDao {

    @Autowired
    public SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int update(String fuelintegrationcode, String accountingintegrationcode, int accountId) {
        String sql = "";
        Object[] param = null;

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {

            sql = "SELECT r_result_id FROM general.process_centralaccountintegrationcodesupdate(?, ?, ?, ?, ?);";
            param = new Object[]{accountingintegrationcode, fuelintegrationcode, accountId, sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};
            try {
                return getJdbcTemplate().queryForObject(sql, param, Integer.class);
            } catch (DataAccessException e) {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        } else {
            sql = "UPDATE general.accountinfo "
                      + "SET "
                      + "fuelintegrationcode=?,  "
                      + "accountingintegrationcode=? , "
                      + "u_id = ?,\n"
                      + "u_time = NOW()\n"
                      + "WHERE account_id=? AND branch_id = ? AND deleted=false";

            param = new Object[]{fuelintegrationcode, accountingintegrationcode, sessionBean.getUser().getId(), accountId, sessionBean.getUser().getLastBranch().getId()};
            try {
                return getJdbcTemplate().update(sql, param);
            } catch (DataAccessException e) {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        }

    }

    @Override
    public AccountInfo find(int accountId) {
        String sql = " SELECT \n"
                  + " ai.id aiid,\n"
                  + " ai.fuelintegrationcode aifuelintegrationcode,\n"
                  + " ai.accountingintegrationcode aiaccountingintegrationcode\n"
                  + "  from general.accountinfo ai\n"
                  + "  WHERE ai.account_id=? AND ai.branch_id = ? AND ai.deleted=false";

        Object[] param = new Object[]{accountId, sessionBean.getUser().getLastBranch().getId()};

        List<AccountInfo> result = getJdbcTemplate().query(sql, param, new AccountDetailMapper());
        return result.size() > 0 ? result.get(0) : new AccountInfo();
    }

}

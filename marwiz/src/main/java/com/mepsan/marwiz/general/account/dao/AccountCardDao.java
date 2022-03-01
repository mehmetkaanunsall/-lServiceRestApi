/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:11:07 AM
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.AccountCard;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountCardDao extends JdbcDaoSupport implements IAccountCardDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AccountCard> findAccountCard(AccountCard accountCard) {
        String sql = "";
        if (accountCard.getAccount().isIsEmployee() == true) {
            sql = "SELECT \n"
                    + "	crd.id as crdid,\n"
                    + "    crd.employee_id as crdaccountid,\n"
                    + "    crd.rfno as crdrfno,\n"
                    + "    crd.status_id as crdstatus_id,\n"
                    + "    sttd.name as sttdname\n"
                    + "FROM general.card crd\n"
                    + "INNER JOIN system.status_dict sttd ON(sttd.status_id =crd.status_id AND sttd.language_id=?)\n"
                    + "\n"
                    + "WHERE crd.deleted=FALSE  and crd.employee_id=?";
        } else {
            sql = "SELECT \n"
                    + "	crd.id as crdid,\n"
                    + "    crd.account_id as crdaccountid,\n"
                    + "    crd.rfno as crdrfno,\n"
                    + "    crd.status_id as crdstatus_id,\n"
                    + "    sttd.name as sttdname\n"
                    + "FROM general.card crd\n"
                    + "INNER JOIN system.status_dict sttd ON(sttd.status_id =crd.status_id AND sttd.language_id=?)\n"
                    + "\n"
                    + "WHERE crd.deleted=FALSE  and crd.account_id=?";
        }

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), accountCard.getAccount().getId()};

        List<AccountCard> result = getJdbcTemplate().query(sql, param, new AccountCardMapper());
        return result;
    }

    @Override
    public int delete(AccountCard obj) {
        String sql = "UPDATE \n"
                + "	general.card \n"
                + "SET \n"
                + "	deleted = TRUE,\n"
                + "   u_id = ?,\n"
                + "   d_time = NOW()\n"
                + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int create(AccountCard obj) {
        String sql;
        if (obj.getAccount().isIsEmployee() == true) {
            sql = "INSERT INTO general.card (employee_id,status_id,rfno,c_id,u_id) VALUES (?, ?, ?, ?, ?) RETURNING id ;";
        } else {
            sql = "INSERT INTO general.card (account_id,status_id,rfno,c_id,u_id) VALUES (?, ?, ?, ? ,?) RETURNING id ;";
        }

        Object[] param = new Object[]{obj.getAccount().getId(), obj.getStatus().getId(), obj.getRfNo(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(AccountCard obj) {
        String sql;
        sql = "UPDATE \n"
                + "  general.card \n"
                + "SET \n"
                + "  status_id = ?,\n"
                + "  rfno = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?";

        Object[] param = new Object[]{obj.getStatus().getId(), obj.getRfNo(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}

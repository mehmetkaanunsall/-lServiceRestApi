/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:11:17
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.categorization.dao.CategorizationMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountCategorizationDao extends JdbcDaoSupport implements IAccountCategorizationDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Categorization> listCategorization(Account obj, Item ci) {
        String sql;
        if (obj.isIsEmployee() == true) {
            sql = "SELECT \n"
                    + "    gct.id AS gctid,\n"
                    + "    gct.name AS gctname,\n"
                    + "    gct.parent_id AS gctparent_id,\n"
                    + "    CAST(COALESCE((SELECT gcac.id FROM general.employee_categorization_con gcac WHERE gcac.account_id = ? AND gcac.categorization_id = gct.id AND gcac.deleted = false), 0) AS boolean) AS gctchecked\n"
                    + "FROM \n"
                    + "	general.categorization gct \n"
                    + "WHERE\n"
                    + "     gct.item_id = ? AND gct.deleted = false";
        } else {
            sql = "SELECT \n"
                    + "    gct.id AS gctid,\n"
                    + "    gct.name AS gctname,\n"
                    + "    gct.parent_id AS gctparent_id,\n"
                    + "    CAST(COALESCE((SELECT gcac.id FROM general.account_categorization_con gcac WHERE gcac.account_id = ? AND gcac.categorization_id = gct.id AND gcac.deleted = false), 0) AS boolean) AS gctchecked\n"
                    + "FROM \n"
                    + "	general.categorization gct \n"
                    + "WHERE\n"
                    + "     gct.item_id = ? AND gct.deleted = false";
        }

        Object[] param = new Object[]{obj.getId(), ci.getId()};

        List<Categorization> result = getJdbcTemplate().query(sql, param, new CategorizationMapper());
        return result;
    }

    @Override
    public int create(AccountCategorizationConnection obj) {
        String sql;
        if (obj.getAccount().isIsEmployee() == true) {
            sql = "INSERT INTO general.employee_categorization_con (categorization_id, account_id, c_id) VALUES (?, ?, ?) RETURNING id ;";
        } else {
            sql = "INSERT INTO general.account_categorization_con (categorization_id, account_id, c_id) VALUES (?, ?, ?) RETURNING id ;";
        }

        Object[] param = new Object[]{obj.getCategorization().getId(), obj.getAccount().getId(),
            sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AccountCategorizationConnection obj) {
        String sql;
        if (obj.getAccount().isIsEmployee() == true) {
            sql = "UPDATE general.employee_categorization_con SET u_id = ?, u_time = now(), deleted = true WHERE categorization_id = ? AND account_id = ? AND deleted = false";
        } else {
            sql = "UPDATE general.account_categorization_con SET u_id = ?, u_time = now(), deleted = true WHERE categorization_id = ? AND account_id = ? AND deleted = false";
        }

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getCategorization().getId(), obj.getAccount().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }


    /* Parametre uyuşmazlığı yüzünden Interface'sin kendisine yeniden yazıldı. */
    @Override
    public int allCreat(Account obj, String choseeCategorizations, Item ci) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int allCreat(AccountCategorizationConnection obj, String choseeCategorizations, Item ci) {

        String sql;

        if (obj.getAccount().isIsEmployee() == true) {
            sql = "INSERT INTO general.employee_categorization_con (categorization_id, account_id, c_id) \n"
                    + "SELECT\n"
                    + "    gct.id,\n"
                    + "    ? AS account_id,\n"
                    + "    ? AS c_id\n"
                    + "FROM\n"
                    + "	general.categorization gct\n"
                    + "WHERE\n"
                    + "	gct.item_id = ? AND gct.deleted = false AND gct.id NOT IN( " + choseeCategorizations + " ) ";
        } else {
            sql = "INSERT INTO general.account_categorization_con (categorization_id, account_id, c_id) \n"
                    + "SELECT\n"
                    + "    gct.id,\n"
                    + "    ? AS account_id,\n"
                    + "    ? AS c_id\n"
                    + "FROM\n"
                    + "	general.categorization gct\n"
                    + "WHERE\n"
                    + "	gct.item_id = ? AND gct.deleted = false AND gct.id NOT IN( " + choseeCategorizations + " ) ";
        }

        Object[] param = new Object[]{obj.getAccount().getId(), sessionBean.getUser().getId(), ci.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int allUpdate(Account obj, String choseeCategorizations) {
        String sql;

        if (obj.isIsEmployee() == true) {
            sql = "UPDATE "
                    + "general.employee_categorization_con "
                    + "SET "
                    + "deleted = true, "
                    + "u_id = ?, "
                    + "u_time = now() "
                    + "WHERE deleted = false AND account_id = ? AND categorization_id IN ( " + choseeCategorizations + " )";
        } else {
            sql = "UPDATE "
                    + "general.account_categorization_con "
                    + "SET "
                    + "deleted = true, "
                    + "u_id = ?, "
                    + "u_time = now() "
                    + "WHERE deleted = false AND account_id = ? AND categorization_id IN ( " + choseeCategorizations + " )";
        }

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    }

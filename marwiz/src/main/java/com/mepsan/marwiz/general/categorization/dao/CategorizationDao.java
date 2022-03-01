/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 10:05:09
 */
package com.mepsan.marwiz.general.categorization.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CategorizationDao extends JdbcDaoSupport implements ICategorizationDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Categorization> listCategorization(Categorization obj) {
        String where = "";

        String sql = " SELECT \n"
                + "           	gct.id AS gctid,\n"
                + "               gct.name AS gctname,\n"
                + "               gct.parent_id AS gctparent_id,\n"
                + "               1 AS tagquantity\n"
                + "           FROM \n"
                + "           	general.categorization gct\n"
                + "           WHERE\n"
                + "           	gct.item_id = ? AND gct.deleted = false " + where;
        Object[] param = new Object[]{obj.getItem().getId()};
        return getJdbcTemplate().query(sql, param, new CategorizationMapper());
    }

    @Override
    public int create(Categorization obj) {
        String sql = "INSERT INTO general.categorization (name,parent_id,item_id,c_id,u_id) VALUES (?,?,?,?,?)  RETURNING id ;";

        Object[] param = new Object[]{obj.getName(), obj.getParentId() == null ? null : obj.getParentId().getId(),
            obj.getItem().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Categorization obj) {
        String sql = "UPDATE general.categorization \n"
                + "   SET \n"
                + "name = ?,"
                + "u_id = ?, "
                + "u_time = now()\n"
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Categorization categorization) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT parent_id FROM general.categorization WHERE parent_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{categorization.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Categorization categorization) {

        String sql = "";
        if (categorization.getItem().getId() == 2) {//Stock
            sql = "UPDATE general.categorization SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                    + "UPDATE inventory.stock_categorization_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND categorization_id=?;\n";
        } else if (categorization.getItem().getId() == 3) {//Account
            sql = "UPDATE general.categorization SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                    + "UPDATE general.account_categorization_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND categorization_id=?;\n";
        } else if (categorization.getItem().getId() == 26) {//Employee
            sql = "UPDATE general.categorization SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                    + "UPDATE general.employee_categorization_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND categorization_id=?;\n";
        }

        Object[] param = new Object[]{sessionBean.getUser().getId(), categorization.getId(), sessionBean.getUser().getId(), categorization.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int addToItem(int itemId, Categorization categorization, String categorizations, String items) {
        String sql = " SELECT r_id FROM general.categorization_addtoitem(?, ?, ?, ?, ?, ?, ?);";
        Object[] param = {itemId, categorization.getId(), categorizations == null ? null : categorizations.equals("") ? null : categorizations, items == null ? null : items.equals("") ? null : items, sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration(), sessionBean.getUser().getId()};
        try {
            //System.out.println("--param---" + Arrays.toString(param));
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String importItemList(String json) {
        String sql = "SELECT r_message FROM general.categorizationitemcontrol(?, ?, ?)";

        Object[] param = new Object[]{json, sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration(), sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
